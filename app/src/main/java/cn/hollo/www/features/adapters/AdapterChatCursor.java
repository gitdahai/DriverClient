package cn.hollo.www.features.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import cn.hollo.www.R;
import cn.hollo.www.app.EmotionDiction;
import cn.hollo.www.content_provider.ModelChatMessage;
import cn.hollo.www.custom_view.RoundedImageView;
import cn.hollo.www.features.activities.ActivityLocationMap;
import cn.hollo.www.features.activities.MessageExportHelper;
import cn.hollo.www.image.ImageProvider;
import cn.hollo.www.image.ImageReceiver;
import cn.hollo.www.image.ImageUtils;
import cn.hollo.www.upyun.OnDownloadFinishListener;
import cn.hollo.www.upyun.Utils;
import cn.hollo.www.upyun.voice.DownloadVoice;
import cn.hollo.www.voice.VoicePlay;
import cn.hollo.www.xmpp.IChatMessage;

/**
 * Created by orson on 14-12-24.
 * 聊天会话适配器
 */
public class AdapterChatCursor extends CursorAdapter {
    private Context context;
    private Resources resources;
    private OserverUpdateCursor observer;
    private VoicePlay paly;
    private Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

    /*******************************************
     * 构造方法
     * @param context
     * @param c
     */
    public AdapterChatCursor(Context context, Cursor c) {
        super(context, c, true);
        this.context = context.getApplicationContext();
        resources = context.getResources();
    }

    /********************************************
     * 页面已经停止运行时，需要停止播放器的播放
     */
    public void onStoped(){
        stopVoicePlay(paly);
    }

    /********************************************
     * 显示位置信息（需要跳转到一个新的页面）
     * @param description
     * @param lat
     * @param lng
     */
    public void showLocation(String description, double lat, double lng){
        Intent intent = new Intent(context, ActivityLocationMap.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Type", ActivityLocationMap.TYPE_SHOW_LOCATION);
        intent.putExtra("Description", description);
        intent.putExtra("Lat", lat);
        intent.putExtra("Lng", lng);
        context.startActivity(intent);
    }

    /*******************************************
     * 创建新的试图
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.chat_item_view, null);
        ItemHolder holder = new ItemHolder(view);
        view.setTag(holder);

        return view;
    }


    /*******************************************
     * 数据绑定
     * @param view
     * @param context
     * @param cursor
     */
    public void bindView(View view, Context context, Cursor cursor) {
        ItemHolder holder = (ItemHolder)view.getTag();
        ModelChatMessage chatMessage = new ModelChatMessage(cursor);
        holder.flushView(chatMessage);
    }

    /*******************************************
     * 设置更新观察者对象
     * @param observer
     */
    public void setOserverUpdateCursor(OserverUpdateCursor observer){
        this.observer = observer;
    }

    /*******************************************************************
     * 需要更新数据表监听器
     */
    public interface OserverUpdateCursor{
        public void onUpdateCursor(ModelChatMessage chatMessage);
    }

    /**=================================================
     * 检查是否需要更新数据表
     * @param chatMessage
     */
    private void updateCursor(ModelChatMessage chatMessage){
        //如果是接收的数据，才需要检查是否需要更新
        if (observer != null){
            observer.onUpdateCursor(chatMessage);
        }
    }

    /***************************************************
     * 播放语音
     * @param
     */
    private void playVoice(VoicePlay paly){
        if (paly == null || paly.getAttach() == null)
            return;

        // 根据传递的参数，获取播放语音文件的本地路径
        ModelChatMessage chatMessage = (ModelChatMessage)paly.getAttach();
        String voicePathName = chatMessage.content;
        String localFullPathname = Utils.getLocalFilePathFullName(context, voicePathName);

        //开始播放
        paly.setFilePath(localFullPathname);
        paly.setOnPlayFinishListener(playVoiceListener);
        paly.play();
    }

    /**--------------------------------------------------
     * 停止播放语音
     * 如果当前存在语音播放对象，而且正在播放
     * 则先停止播放，之后在释放资源；
     *
     * 这里还是需要修改数据的状态为默认状态，并且更新
     * 数据库
     */
    protected void stopVoicePlay(VoicePlay paly){
        //如果当前有正在播放的语音，则首先停止，并且释放资源
        if (paly != null){
            paly.stop();
            paly.release();

            //获取赞存的附件
            ModelChatMessage chatMessage = (ModelChatMessage)paly.getAttach();
            //如果存在该附件数据，则需要修改该信息的状态
            if (chatMessage != null){
                chatMessage.showState = ModelChatMessage.SHOW_STATE_NONE;
                updateCursor(chatMessage);
            }
        }
    }

    /***************************************************
     * 语音播放监听器
     */
    private VoicePlay.OnPlayFinishListener playVoiceListener = new VoicePlay.OnPlayFinishListener(){
        public void onPlayFinish(String filePathName, int id) {
            stopVoicePlay(paly);
        }
        public void onPlayError(String filePathName, int id) {
            stopVoicePlay(paly);
        }
        public void onPlayStop(String filePathName, int id) {stopVoicePlay(paly);}
    };

    /*******************************************************************
     * 缓存的试图项
     */
    private class ItemHolder {
        protected int textMaxSize = 16;     //文本的最大字号
        protected int textMinSize = 12;     //文本的最小字号

        protected LinearLayout chatContainer;     //对话容器
        protected LinearLayout contentContainer;  //消息实体容器
        protected FrameLayout  contentEntities;   //消息实体
        protected ImageView    contentStatus;     //消息的状态
        protected TextView     contentTextView;   //显示消息内容
        protected ImageView    contentImgView;    //显示图片，动画等内容
        protected RoundedImageView userAvatar;    //头像
        protected ModelChatMessage chatMessage;

        private ItemHandler    issueHandler;
        private ItemHandler    receiverHandler;

        /**==================================================
         *
         * @param view
         */
        private ItemHolder(View view){
            chatContainer = (LinearLayout)view.findViewById(R.id.chatContainer);
            contentContainer = (LinearLayout)view.findViewById(R.id.contentContainer);
            contentEntities = (FrameLayout)view.findViewById(R.id.contentEntities);
            contentStatus = (ImageView)view.findViewById(R.id.contentStatus);
            contentTextView = (TextView)view.findViewById(R.id.contentTextView);
            contentImgView = (ImageView)view.findViewById(R.id.contentImgView);
            userAvatar = (RoundedImageView)view.findViewById(R.id.userAvatar);

            issueHandler = new ItemIssueHandler(this);
            receiverHandler = new ItemReceiverHandler(this);
        }

        /**==================================================
         * 显示(刷新)数据
         * @param chatMessage
         */
        private void flushView(ModelChatMessage chatMessage){
            this.chatMessage = chatMessage;
            initDataToView();

            //如果是自己发送的消息，则走IssueHandler处理流程
            if (chatMessage.isIssue){
                contentStatus.setOnClickListener(issueHandler);
                contentImgView.setOnClickListener(issueHandler);
                contentTextView.setOnClickListener(issueHandler);
                issueHandler.showItemView();
            }
            //否则则走ReceiverHandler处理流程
            else{
                contentStatus.setOnClickListener(receiverHandler);
                contentImgView.setOnClickListener(receiverHandler);
                contentTextView.setOnClickListener(receiverHandler);
                receiverHandler.showItemView();
            }
        }

        /**==================================================
         * 清除试图上的残留数据
         */
        private void initDataToView(){
            contentStatus.setVisibility(View.GONE);
            contentTextView.setVisibility(View.GONE);
            contentImgView.setVisibility(View.GONE);
        }
    }

    /*****************************************************************
     *
     * 对话（聊天）消息显示项
     * 这里是一些公共的方法
     *****************************************************************/
    private abstract class ItemHandler implements View.OnClickListener,
            OnDownloadFinishListener, ImageReceiver{

        protected ItemHolder item;

        /**-------------------------------------------------
         * 构造
         * @param item
         */
        private ItemHandler(ItemHolder item){
         this.item = item;
        }

        /**---------------------------------------------------
         * 显示试图
         */
        public void showItemView() {
            //试图重新布局
            reLayout();
            //显示头像
            showAvatar();
            //显示或者隐藏消息的状态
            showMessageStatus();
            //分派消息
            assignMessage();
        }

        /**---------------------------------------------------
         * 分派消息的处理方法
         *
         * 根据当前消息的类型来过滤消息，别且分发到不同的方法中去继续处理
         * 目前支持的消息的类型有：
         *  Message         : 纯文本消息
         *  AudioMessage    : 音频消息
         *  EmotionMessage  : 表情消息
         *  ImageMessage    : 图片消息
         *  LocationMessage : 位置消息
         *
         */
        private void assignMessage(){
            //分派文本消息
            if (IChatMessage.PLAIN_MESSAGE.equals(item.chatMessage.messageType))
                handleTextMessage();
                //分派语音消息
            else if (IChatMessage.AUDIO_MESSAGE.equals(item.chatMessage.messageType))
                handleAudioMessage();
                //分派表情消息
            else if (IChatMessage.EMOTION_MESSAGE.equals(item.chatMessage.messageType))
                handleEmotionMessage();
                //分派图片消息
            else if (IChatMessage.IMAGE_MESSAGE.equals(item.chatMessage.messageType))
                handleImageMessage();
                //分派位置消息
            else if (IChatMessage.LOCATION_MESSAGE.equals(item.chatMessage.messageType))
                handleLocationMessage();
        }

        /**-------------------------------------------------
         * 当加载图片资源完成后，会调用该方法
         * @param code
         * @param bm
         * @param attach
         */
        public void onImagePush(int code, Bitmap bm, Object attach) {
            //如果成功取得头像，图片则保存在容器中
            if (bm != null){
                String bmPathName = (String)attach;
                SoftReference<Bitmap> softReference = new SoftReference<Bitmap>(bm);
                String bmFileName = ImageUtils.extractFileName(bmPathName);
                imageCache.put(bmFileName, softReference);
                //通知数据更新，刷新
                AdapterChatCursor.this.notifyDataSetChanged();
            }
        }

        /**-------------------------------------------------
         * 处理表情符号消息
         */
        private void handleEmotionMessage(){
            if (item.chatMessage.content != null){
                EmotionDiction emotionDiction = EmotionDiction.getInstance(context);
                Integer drawableId = emotionDiction.getMotionIconResId(item.chatMessage.content);

                //显示表情
                if (drawableId != null){
                    item.contentImgView.setVisibility(View.VISIBLE);
                    item.contentImgView.setImageResource(drawableId);
                    item.contentImgView.setBackgroundResource(0);
                }
            }
        }

        /**-------------------------------------------------
         *　处理图拍消息
         */
        protected void handleImageMessage(){
            item.contentImgView.setVisibility(View.VISIBLE);
            item.contentImgView.setImageResource(R.drawable.message_tupian_default);
            item.contentImgView.setBackgroundResource(0);

            //如果图片资源的路径不存在，则直接返回
            if (item.chatMessage.content == null || "".equals(item.chatMessage.content))
                return;

            //从头像路径中，抽取文件名称
            String fileName = ImageUtils.extractFileName(item.chatMessage.content);

            //如果该资源图片还没有加载，则需要进行加载
            if (item.chatMessage.messageStatus == ModelChatMessage.STATUS_NONE_TRANSFER){
                //修改状态为接收中
                item.chatMessage.messageStatus = ModelChatMessage.STATUS_TRANSFERING;
                //通知跟新状态
                updateCursor(item.chatMessage);

                //再去加载实际的头像图片
                ImageProvider provider = ImageProvider.getInstance(context);
                provider.getThumbnailImage(item.chatMessage.content, imageReceiver, item.chatMessage);
            }
            //如果已经加载过该图片资源，则进行缩略图的显示
            else if (item.chatMessage.messageStatus == ModelChatMessage.STATUS_TRANSFER_SUCCED){
                //从缓冲区中读取Bitma对象
                SoftReference<Bitmap> softReference = imageCache.get(fileName);
                //如果该资源已经在缓存中存在了，则直接使用
                if (softReference != null && softReference.get() != null)
                    item.contentImgView.setImageBitmap(softReference.get());
                else{
                    //再去加载实际的头像图片
                    ImageProvider provider = ImageProvider.getInstance(context);
                    provider.getThumbnailImage(item.chatMessage.content, imageReceiver, item.chatMessage);
                }
            }
        }

        /**-------------------------------------------------
         *　处理位置消息
         */
        protected void handleLocationMessage(){
            item.contentTextView.setVisibility(View.VISIBLE);
            item.contentTextView.setTextColor(resources.getColor(R.color.color_black));

            item.contentTextView.getLayoutParams().width = 200;
            item.contentTextView.setGravity(Gravity.BOTTOM | Gravity.LEFT);
            item.contentTextView.setBackgroundResource(R.drawable.location_place2x);
            item.contentTextView.setText(item.chatMessage.content);
            item.contentTextView.setPadding(8, 0, 8, 0);
            item.contentTextView.setTextSize(item.textMinSize);
        }

        /**------------------------------------------------
         * 加载图片返回
         */
        private ImageReceiver imageReceiver = new ImageReceiver(){
            public void onImagePush(int code, Bitmap bm, Object attach) {
                if (bm != null && attach != null){
                    ModelChatMessage chatMessage = (ModelChatMessage)attach;
                    String bmFileName = ImageUtils.extractFileName(chatMessage.content);
                    SoftReference<Bitmap> softReference = new SoftReference<Bitmap>(bm);
                    imageCache.put(bmFileName, softReference);
                    //修改状态为成功
                    item.chatMessage.messageStatus = ModelChatMessage.STATUS_TRANSFER_SUCCED;
                    //通知跟新状态
                    updateCursor(item.chatMessage);
                }
                else{
                    //修改状态为失败
                    item.chatMessage.messageStatus = ModelChatMessage.STATUS_TRANSFER_FAIL;
                    //通知跟新状态
                    updateCursor(item.chatMessage);
                }
            }
        };

        /**-------------------------------------------------
         * 点击产生的事件，可能的事件有：
         * 点击播放语音，发达图片。。。
         * 根据消息的类型，分派相应的动作
         * @param v
         */
        public void onClick(View v) {
            int viewId = v.getId();

            //如果是在ImageView产生的事件，则需要执行处理跟图片相关的事件
            if (viewId == item.contentImgView.getId())
                onActionClickContentImageVew();

            //否则执行跟文本相关的事件
            else if (viewId == item.contentTextView.getId())
                onActionClickContentTextView();

            //状态点击事件
            else if (viewId == item.contentStatus.getId() && item.chatMessage.messageStatus == ModelChatMessage.STATUS_TRANSFER_FAIL)
                onRepeatTransfer();
        }

        /**-------------------------------------------------
         * 在ImageView上，产生的事件
         */
        private void onActionClickContentImageVew(){
            //分派语音消息
            if (IChatMessage.AUDIO_MESSAGE.equals(item.chatMessage.messageType))
                onActionPlayVoice();
                //分派图片消息
            else if (IChatMessage.IMAGE_MESSAGE.equals(item.chatMessage.messageType))
                onActionShowImg();
        }

        /**-------------------------------------------------
         * 在TextView上产生的事件
         * 如果当前的消息类型是关于“位置”的，则需要处理该事件，
         * 也就是打开地图，显示该坐标位置信息;
         * 其他消息不做处理
         */
        private void onActionClickContentTextView(){
            //如果是位置信息
            if (IChatMessage.LOCATION_MESSAGE.equals(item.chatMessage.messageType)){
                onActionShowLocation();
            }
        }

        /**------------------------------------------------
         * 执行显示位置信息的动作
         */
        private void onActionShowLocation(){
            showLocation(item.chatMessage.description, item.chatMessage.latitude, item.chatMessage.longitude);
        }

        /**-------------------------------------------------
         * 当下载语音结束后，会调用该方法
         * @param code
         * @param isSuccess
         * @param saveFileName
         * @param attach
         */
        public void onDownloadFinish(int code, boolean isSuccess, String saveFileName, Object attach) {
            if (attach == null)
                return;

            ModelChatMessage chatMessage = (ModelChatMessage)attach;

            //下载成功
            if (code == 200)
                chatMessage.messageStatus = ModelChatMessage.STATUS_TRANSFER_SUCCED;
            else
                chatMessage.messageStatus = ModelChatMessage.STATUS_TRANSFER_FAIL;

            //通知跟新状态
            updateCursor(chatMessage);
        }

        /**重新布局试图*/
        protected void reLayout(){}
        /**显示状态*/
        protected void showMessageStatus(){}
        /**显示用户的头像*/
        protected void showAvatar(){}
        /**分派处理文本消息*/
        protected void handleTextMessage(){}
        /**处理分派的语音任务*/
        protected void handleAudioMessage(){}
        /**处理分派过来的图片任务*/
        //protected void handleImageMessage(){}
        /**处理分派过来的位置信息*/
        //protected void handleLocationMessage(){}
        /***执行播放语音的动作*/
        protected void onActionPlayVoice(){}
        /**执行显示图片的动作*/
        protected void onActionShowImg(){}
        /**
         * 重新传输数据
         * 当消息的状态被点击后，需要分发到
         * “接收”或者“发送”端，进行测试，
         * 如果当前的状态是“传输失败”的，则该事件需要传新传输
         */
        protected void onRepeatTransfer(){}
    }

    /*****************************************************************
     * 这里是用户发出的消息
     * 处理消息流程
     *
     *
     *****************************************************************/
    private class ItemIssueHandler extends ItemHandler{
        /**----------------------------------------------------
         * 构造
         * @param item
         */
        private ItemIssueHandler(ItemHolder item) {
            super(item);
        }

        /**---------------------------------------------------
         * 对试图进行重新布局
         *
         * 因为不管是自己发送的信息，还是接收到的消息，
         * 都是使用同一个Item试图来显示的，所以在布局
         * 上应该区分出来该消息是自己发送的，还是接收
         * 的，因此每次显示item试图时，都需要根据实际
         * 的消息发送者来区分是接收的，还是发送的，来布局
         * 试图显示。
         */
        protected void reLayout(){
            //清空整个item容器
            item.chatContainer.removeAllViews();
            //清空消息实体布局
            item.contentContainer.removeAllViews();
            //右向对齐
            item.contentContainer.setGravity(Gravity.RIGHT);

            //---------重新组装试图-------------------
            //添加状态试图
            item.contentContainer.addView(item.contentStatus);
            //添加显示消息内容的实体布局
            item.contentContainer.addView(item.contentEntities);
            //添加消息的容器
            item.chatContainer.addView(item.contentContainer);
            //添加头像
            item.chatContainer.addView(item.userAvatar);
        }

        /**---------------------------------------------------
         * 显示当前消息的状态
         *
         * 根据当前发送消息的状态，用一个状态标志来显示该
         * 消息的状态，用于快速识别该消息的当前正在处于的状态。
         *
         * 如果当前的消息的状态为0，则表示正在发送中;
         * 如果当前的消息的状态为1, 则表示该消息已经成功发送了;
         * 如果当前的消息的状态为2, 则表示该消息上传失败了;
         * 如果当前的消息的状态为3, 则表示该消息没有发送过（对于发送者，该类型的状态没有定义）
         */
        protected void showMessageStatus(){
            //启用状态标志的上传动画
            if (item.chatMessage.messageStatus == ModelChatMessage.STATUS_TRANSFERING){
                item.contentStatus.setVisibility(View.VISIBLE);
                item.contentStatus.setImageResource(R.drawable.stat_sys_upload_anim);
                //执行动画
                AnimationDrawable animationDrawable = (AnimationDrawable) item.contentStatus.getDrawable();
                //启动动画
                if (animationDrawable != null)
                    animationDrawable.start();
            }
            //显示上传失败的状态
            else if (item.chatMessage.messageStatus == ModelChatMessage.STATUS_TRANSFER_FAIL){
                item.contentStatus.setVisibility(View.VISIBLE);
                item.contentStatus.setImageResource(R.drawable.indicator_input_error);
            }
        }

        /**---------------------------------------------------
         * 显示头像
         * 因为该消息是司机自己发送出去的，所以用一个
         * 默认头像来显示
         */
        protected void showAvatar(){
            item.userAvatar.setImageResource(R.drawable.banche);
        }

        /**--------------------------------------------------
         * 处理文本消息
         *
         * 文本消息直接显示出来即可，但是在显示出来之前
         * 需要设置“对话气泡”的样式为“发出”的样式;还要
         * 设置显示的字体颜色为白色.
         * 是该试图变为可见状态;
         * 同时需要隐藏其他内容的试图对象.
         */
        protected void handleTextMessage(){
            item.contentTextView.getLayoutParams().width = FrameLayout.LayoutParams.WRAP_CONTENT;
            item.contentTextView.setBackgroundResource(R.drawable.message_r);
            item.contentTextView.setTextColor(resources.getColor(R.color.color_white));
            item.contentTextView.setText(item.chatMessage.content);
            item.contentTextView.setVisibility(View.VISIBLE);
            item.contentTextView.setTextSize(item.textMaxSize);
            item.contentTextView.setGravity(Gravity.LEFT);
        }

        /**--------------------------------------------------
         * 处理语音消息
         *
         * 该语音消息需要显示成“声波”样式的图片,并且也是包含在
         * 会话“气泡”内;如果当前的是正在播放语音，则需要加载语音动画
         * 文件，进行动画的播放
         * 使该试图变为可见状态;
         * 需要隐藏文本试图
         */
        protected void handleAudioMessage(){
            item.contentImgView.setBackgroundResource(R.drawable.message_r);
            item.contentImgView.setVisibility(View.VISIBLE);

            //如果当前状态是正在播放，则直接返回
            if (item.chatMessage.showState == ModelChatMessage.SHOW_STATE_PLAYING){
                //设置动画文件
                item.contentImgView.setImageResource(R.drawable.voice_play_right);
                //执行动画
                AnimationDrawable animationDrawable = (AnimationDrawable) item.contentImgView.getDrawable();
                //启动动画
                if (animationDrawable != null)
                    animationDrawable.start();
            }
            //否则直接显示静止图片
            else{
                item.contentImgView.setImageResource(R.drawable.voice_play_right_0);
            }
        }

        /**-------------------------------------------------
         * 如果当前没有正在播放的语音，则直接进行播放
         * 否则先停止上一个语音文件的播放流程，在开启新的
         * 播放，同时开启动态显示正在播放的语音，同时跟新
         * 数据库，将当前要播放的语音状态跟新为“播放中”，
         * 同时已经停止的播放语音也要跟新数据库，跟新为“正常状态”;
         *
         * 注意：如果当前的语音的状态是“正在播放”，则需要停止播放
         */
        protected void onActionPlayVoice(){
            //判断，如果当前该消息正在播放，
            // 则这次需要执行停止操作
            //停止之后，直接退出
            if (item.chatMessage.showState == ModelChatMessage.SHOW_STATE_PLAYING){
                stopVoicePlay(paly);
                return;
            }

            //如果当前有其他的语音在播放，则也是需要停止
            if (paly != null && paly.isPlaying()){
                stopVoicePlay(paly);
            }

            //开始一个新的播放流程
            item.chatMessage.showState = ModelChatMessage.SHOW_STATE_PLAYING;
            item.chatMessage.updateToDatabase(context);

            //进行播放
            paly = new VoicePlay();
            paly.setAttach(item.chatMessage);
            playVoice(paly);
        }

        /**-------------------------------------------------
         * 检查是否需要重新发送数据
         */
        protected  void onRepeatTransfer(){
            ModelChatMessage chatMessage = item.chatMessage;
            MessageExportHelper helper = new MessageExportHelper(context, chatMessage.roomId);

            //是否是文本消息
            if (ModelChatMessage.PLAIN_MESSAGE.equals(chatMessage.messageType))
                helper.reExportText(chatMessage);
            //是否是位置信息
            else if (ModelChatMessage.LOCATION_MESSAGE.equals(chatMessage.messageType))
                helper.reExportLocation(chatMessage);
            //是否语音信息
            else if (ModelChatMessage.AUDIO_MESSAGE.equals(chatMessage.messageType))
                helper.reExportVoice(chatMessage);
        }
    }

    /**********************************************************************
     * 处理接收到的消息流程
     * 处理的功能包括：
     * 调整布局，显示会话内容，下载接收资源
     * 播放语音，显示图片
     *
     *
     **********************************************************************/
    private class ItemReceiverHandler extends ItemHandler{
        /**--------------------------------------------------
         * 构造
         */
        private ItemReceiverHandler(ItemHolder item) {
            super(item);
        }

        /**---------------------------------------------------
         * 重新布局
         *
         * 该消息是接收过来的，因此需要把显示消息内容的布局该变成
         * “靠左显示”的样式
         */
        protected void reLayout(){
            //清空整个item容器
            item.chatContainer.removeAllViews();
            //清空消息实体布局
            item.contentContainer.removeAllViews();
            //设置成左侧对齐
            item.contentContainer.setGravity(Gravity.LEFT);

            //--------------重新组装试图-------------------
            //添加显示消息内容的实体布局
            item.contentContainer.addView(item.contentEntities);
            //添加状态
            item.contentContainer.addView(item.contentStatus);
            //添加头像
            item.chatContainer.addView(item.userAvatar);
            //添加消息的容器
            item.chatContainer.addView(item.contentContainer);
        }

        /**---------------------------------------------------
         * 显示发送者的头像
         *
         * 首先检查该头像是否在本地缓存过，如果缓存过，则直接取出使用,
         * 如果缓存中没有，再去本地（磁盘）加载，如果本地也不存在该用户
         * 的头像资源，则需要进行远程加载，需要根据图片的地址，到upYun
         * 上去下载，同时在界面的显示上，先用一个默认的头像来代替该用户
         * 的头像，进行显示，具体使用那个默认头像，则是根据该用户的性别
         * 进行判断的。
         *
         * 如果从远程加载头像资源成功后，把它存放到“缓存”中，同时需要通
         * 知适配器，用户的消息的状态已经改变了，需要重新刷新ui试图。
         */
        protected void showAvatar(){
            //如果该用户不存在头像，则直接用一个默认头像来替代
            if (item.chatMessage.avatar == null || "".equals(item.chatMessage.avatar)){
                //根据性别分别设置成“男／女”头像
                if (item.chatMessage.gender == 2)
                    item.userAvatar.setImageResource(R.drawable.female_avatar);
                else
                    item.userAvatar.setImageResource(R.drawable.avatar);

                return;
            }

            //从头像路径中，抽取文件名称
            String fileName = ImageUtils.extractFileName(item.chatMessage.avatar);

            //从缓冲区中读取Bitma对象
            SoftReference<Bitmap> softReference = imageCache.get(fileName);

            //如果已经存在一个位图
            if (softReference != null && softReference.get() != null)
                item.userAvatar.setImageBitmap(softReference.get());
                //否则去加载图片
            else{
                //在图片加载完成之前，先使用一个默认的头像图片来替代
                //根据性别分别设置成“男／女”头像
                if (item.chatMessage.gender == 2)
                    item.userAvatar.setImageResource(R.drawable.female_avatar);
                else
                    item.userAvatar.setImageResource(R.drawable.avatar);

                //再去加载实际的头像图片
                ImageProvider provider = ImageProvider.getInstance(context);
                provider.getThumbnailImage(item.chatMessage.avatar, this, item.chatMessage.avatar);
            }
        }

        /**---------------------------------------------------
         * 显示或者隐藏消息的状态
         *
         * 设置当前消息的状态，接收过来的消息包含的消息状态有：
         * 如果当前的消息的状态为0，则表示正在接收中;
         * 如果当前的消息的状态为1, 则表示该消息已经成功接收了;
         * 如果当前的消息的状态为2, 则表示该消息下载失败了;
         * 如果当前的消息的状态为3, 则表示该消息没有接收过
         */
        protected void showMessageStatus(){
            //如果正在发送/接收，则显示上传动画
            if (item.chatMessage.messageStatus == ModelChatMessage.STATUS_TRANSFERING){
                item.contentStatus.setVisibility(View.VISIBLE);
                item.contentStatus.setImageResource(R.drawable.stat_sys_download_anim);

                //执行动画
                AnimationDrawable animationDrawable = (AnimationDrawable) item.contentStatus.getDrawable();
                //启动动画
                if (animationDrawable != null)
                    animationDrawable.start();
            }
            //如果上传／下载失败，则显示叹号
            else if (item.chatMessage.messageStatus == ModelChatMessage.STATUS_TRANSFER_FAIL){
                item.contentStatus.setVisibility(View.VISIBLE);
                item.contentStatus.setImageResource(R.drawable.indicator_input_error);
            }
        }

        /**--------------------------------------------------
         * 处理文本消息
         *
         * 文本消息直接显示出来即可，但是在显示出来之前
         * 需要设置“对话气泡”的样式为“发出”的样式;还要
         * 设置显示的字体颜色为白色.
         * 是该试图变为可见状态;
         * 同时需要隐藏其他内容的试图对象.
         *
         * 因为是接收过来的消息，因此显示完成文本之后，
         * 需要跟行数据库，改变状态为“已读”状态
         */
        protected void handleTextMessage(){
            item.contentTextView.getLayoutParams().width = FrameLayout.LayoutParams.WRAP_CONTENT;
            item.contentTextView.setBackgroundResource(R.drawable.message_l);
            item.contentTextView.setTextColor(resources.getColor(R.color.color_black));
            item.contentTextView.setText(item.chatMessage.content);
            item.contentTextView.setTextSize(item.textMaxSize);
            item.contentTextView.setGravity(Gravity.LEFT);
            item.contentTextView.setVisibility(View.VISIBLE);

            if(!item.chatMessage.isRead){
                //设置成“已读”状态
                item.chatMessage.isRead = true;
                //跟新到数据库
                item.chatMessage.updateToDatabase(context);
            }
        }

        /**--------------------------------------------------
         * 处理语音消息
         *
         * 该语音消息需要显示成“声波”样式的图片,并且也是包含在
         * 会话“气泡”内;
         * 使该试图变为可见状态;
         * 需要隐藏文本试图
         *
         * 如果该语音消息还没有接收，则进行自动下载
         */
        protected void handleAudioMessage(){
            item.contentImgView.setBackgroundResource(R.drawable.message_l);
            item.contentImgView.setVisibility(View.VISIBLE);

            if (item.chatMessage.showState == ModelChatMessage.SHOW_STATE_PLAYING){
                //设置动画文件
                item.contentImgView.setImageResource(R.drawable.voice_play_left);
                //执行动画
                AnimationDrawable animationDrawable = (AnimationDrawable) item.contentImgView.getDrawable();
                //启动动画
                if (animationDrawable != null)
                    animationDrawable.start();
            }
            else{
                item.contentImgView.setImageResource(R.drawable.voice_play_left_0);

                //如果资源还没有接收，则进行自动下载
                if (item.chatMessage.messageStatus == ModelChatMessage.STATUS_NONE_TRANSFER && item.chatMessage.content != null){
                    //修改状态为接收中
                    item.chatMessage.messageStatus = ModelChatMessage.STATUS_TRANSFERING;
                    //通知跟新状态
                    updateCursor(item.chatMessage);
                    //开始下载语音资源
                    String localFullPathname = Utils.getLocalFilePathFullName(context, item.chatMessage.content);
                    DownloadVoice downloadVoice = new DownloadVoice(item.chatMessage.content, localFullPathname);
                    downloadVoice.setAttachment(item.chatMessage);
                    downloadVoice.setOnDownloadFinishListener(this);
                    downloadVoice.excuteRequest();
                }
            }
        }

        /**-------------------------------------------------
         * 播放语音动作
         */
        protected void onActionPlayVoice(){
            //判断，如果当前该消息正在播放，
            // 则这次需要执行停止操作
            //停止之后，直接退出
            if (item.chatMessage.showState == ModelChatMessage.SHOW_STATE_PLAYING){
                stopVoicePlay(paly);
                return;
            }

            //如果当前有其他的语音在播放，则也是需要停止
            if (paly != null && paly.isPlaying()){
                stopVoicePlay(paly);
            }

            //如果该语音文件还没有接收成功，则不能进行播放
            if (item.chatMessage.messageStatus != ModelChatMessage.STATUS_TRANSFER_SUCCED){
                return;
            }

            //开始一个新的播放流程
            item.chatMessage.showState = ModelChatMessage.SHOW_STATE_PLAYING;
            item.chatMessage.isRead = true;
            item.chatMessage.updateToDatabase(context);

            //进行播放
            paly = new VoicePlay();
            paly.setAttach(item.chatMessage);
            playVoice(paly);
        }

        /**-------------------------------------------------
         * 检查是否需要重新发送数据
         */
        protected  void onRepeatTransfer(){
            ModelChatMessage chatMessage = item.chatMessage;

            if (ModelChatMessage.AUDIO_MESSAGE.equals(chatMessage.messageType) ||
                    ModelChatMessage.IMAGE_MESSAGE.equals(chatMessage.messageType)){

                chatMessage.messageStatus = ModelChatMessage.STATUS_NONE_TRANSFER;
                chatMessage.updateToDatabase(context);
            }
        }
    }
}
