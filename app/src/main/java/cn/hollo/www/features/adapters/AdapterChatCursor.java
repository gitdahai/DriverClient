package cn.hollo.www.features.adapters;

import android.content.Context;
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
import cn.hollo.www.content_provider.ModelChatMessage;
import cn.hollo.www.custom_view.RoundedImageView;
import cn.hollo.www.image.ImageProvider;
import cn.hollo.www.image.ImageReceiver;
import cn.hollo.www.xmpp.IChatMessage;

/**
 * Created by orson on 14-12-24.
 * 聊天会话适配器
 */
public class AdapterChatCursor extends CursorAdapter {
    private Context context;
    private Resources resources;
    private OserverUpdateCursor observer;
    private Map<Long, SoftReference<Bitmap>> imageCache = new HashMap<Long, SoftReference<Bitmap>>();

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
        holder.showData(chatMessage);
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

    /*******************************************************************
     * 缓存的试图项
     */
    private class ItemHolder implements ImageReceiver {
        private LinearLayout chatContainer;     //对话容器
        private LinearLayout contentContainer;  //消息实体容器
        private FrameLayout  contentEntities;   //消息实体
        private ImageView    contentStatus;     //消息的状态
        private TextView     contentTextView;   //显示消息内容
        private ImageView    contentImgView;    //显示图片，动画等内容
        private RoundedImageView userAvatar;    //头像


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
        }

        /**==================================================
         * 显示数据
         * @param chatMessage
         */
        private void showData(ModelChatMessage chatMessage){
            System.out.println("==========message--status========" + chatMessage.message_status);
            //首先隐藏子试图
            hideAllChildViews();
            //显示对话者的头像
            showAvatar(chatMessage);
            //首先改变布局
            changeChatLyout(chatMessage.is_issue);
            //改变消息显示文本的背景
            changeTextBg(chatMessage);
            //改变当前消息的状态
            changeMessageState(chatMessage.message_status, chatMessage.is_issue);
            //显示消息的内容
            showMessageContent(chatMessage);
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

        /**==================================================
         * 隐藏部分子试图
         */
        private void hideAllChildViews(){
            contentStatus.setVisibility(View.GONE);
            contentTextView.setVisibility(View.GONE);
            contentImgView.setVisibility(View.GONE);
        }

        /**==================================================
         * 显示头像
         */
        private void showAvatar(ModelChatMessage chatMessage){
            //如果是自己发送的消息，则使用本地默认头像
            if (chatMessage.is_issue)
                userAvatar.setImageResource(R.drawable.banche);
            //否则加载头像
            else {
                //从缓冲区中读取Bitma对象
                SoftReference<Bitmap> softReference = imageCache.get(chatMessage.timestamp);

                //如果已经存在一个位图
                if (softReference != null && softReference.get() != null)
                    userAvatar.setImageBitmap(softReference.get());
                //否则去加载图片
                else{
                    //在图片加载完成之前，先使用一个默认的头像图片来替代
                    //根据性别分别设置成“男／女”头像
                    if (chatMessage.gender == 2)
                        userAvatar.setImageResource(R.drawable.female_avatar);
                    else
                        userAvatar.setImageResource(R.drawable.avatar);

                    //再去加载实际的头像图片
                    ImageProvider provider = ImageProvider.getInstance(context);
                    provider.getThumbnailImage(chatMessage.avatar, this, chatMessage.timestamp);
                }
            }
        }

        /**==================================================
         * 改变布局
         */
        private void changeChatLyout(boolean is_issue){
            chatContainer.removeAllViews();
            contentContainer.removeAllViews();

            //如果为true，则该消息是发送出去的(右边显示)
            if (is_issue){
                contentContainer.setGravity(Gravity.RIGHT);
                //添加状态
                contentContainer.addView(contentStatus);
                //添加显示消息内容的实体布局
                contentContainer.addView(contentEntities);
                //添加消息的容器
                chatContainer.addView(contentContainer);
                //添加头像
                chatContainer.addView(userAvatar);
            }
            //否则是接收过来的(左边显示)
            else{
                contentContainer.setGravity(Gravity.LEFT);
                //添加显示消息内容的实体布局
                contentContainer.addView(contentEntities);
                //添加状态
                contentContainer.addView(contentStatus);
                //添加头像
                chatContainer.addView(userAvatar);
                //添加消息的容器
                chatContainer.addView(contentContainer);
            }
        }

        /**==================================================
         * 根据消息的类型，改变试图的样式
         * @param chatMessage
         */
        private void changeTextBg(ModelChatMessage chatMessage){
            //如果是文本信息
            if (IChatMessage.PLAIN_MESSAGE.equals(chatMessage.message_type)){
                if (chatMessage.is_issue){
                    contentTextView.setBackgroundResource(R.drawable.message_r);
                    contentTextView.setTextColor(resources.getColor(R.color.color_white));
                }
                else{
                    contentTextView.setBackgroundResource(R.drawable.message_l);
                    contentTextView.setTextColor(resources.getColor(R.color.color_black));
                }
            }
            //否则设置图片显示容器
            else {
                if (chatMessage.is_issue){
                    contentImgView.setBackgroundResource(R.drawable.message_r);
                    contentImgView.setImageResource(R.drawable.voice_play_right_0);
                }
                else{
                    contentImgView.setBackgroundResource(R.drawable.message_l);
                    contentImgView.setImageResource(R.drawable.voice_play_left_0);
                }
            }
        }

        /**==================================================
         * 改变消息文本的状态
         * @param messageStatus
         * @param is_issue
         */
        private void changeMessageState(int messageStatus, boolean is_issue){
            //如果正在发送/接收，则显示上传动画
            if (messageStatus == 0){
                //如果是发出的，则设置“上传动态”
                if (is_issue)
                    contentStatus.setImageResource(R.drawable.stat_sys_upload_anim);
                //否则设置成“下载动态”
                else
                    contentStatus.setImageResource(R.drawable.stat_sys_download_anim);

                //显示状态试图
                contentStatus.setVisibility(View.VISIBLE);

                //执行动画
                AnimationDrawable animationDrawable = (AnimationDrawable) contentStatus.getDrawable();
                //启动动画
                if (animationDrawable != null)
                    animationDrawable.start();
            }

            //如果已经上传成功，则取消任何状态的显示
            else if (messageStatus == 1){
                contentStatus.setImageResource(0);
            }

            //如果上传失败，则显示叹号
            else if (messageStatus == 2){
                contentStatus.setImageResource(R.drawable.indicator_input_error);
            }
        }

        /**==================================================
         * 显示消息实体的内容
         */
        private void showMessageContent(ModelChatMessage chatMessage){
            //春文本消息
            if (IChatMessage.PLAIN_MESSAGE.equals(chatMessage.message_type))
                showTextMessage(chatMessage);

            //语音消息
            else if (IChatMessage.AUDIO_MESSAGE.equals(chatMessage.message_type))
                showAudioMessage(chatMessage);

        }

        /**==================================================
         * 显示文本信息
         * @param chatMessage
         */
        private void showTextMessage(ModelChatMessage chatMessage){
            contentTextView.setText("");
            contentTextView.setText(chatMessage.content);
            contentTextView.setVisibility(View.VISIBLE);

            //如果是接收其他用户发送的文本，则需要跟新数据状态
            if (!chatMessage.is_issue){
                if(!chatMessage.is_read && observer != null){
                    //设置成“已读”状态
                    chatMessage.is_read = true;
                    //跟新到数据库
                    observer.onUpdateCursor(chatMessage);
                }
            }
        }

        /**==================================================
         * 显示语音消息
         * @param chatMessage
         */
        private void showAudioMessage(ModelChatMessage chatMessage){
            contentImgView.setVisibility(View.VISIBLE);


        }

        /**===================================================
         * 加载图片的结果在这里返回了
         * @param code
         * @param bm
         * @param attach
         */
        public void onImagePush(int code, Bitmap bm, Object attach) {
            //如果成功取得头像，图片则保存在容器中
            if (bm != null){
                SoftReference<Bitmap> softReference = new SoftReference<Bitmap>(bm);
                imageCache.put((Long)attach, softReference);
                //通知数据更新，刷新
                AdapterChatCursor.this.notifyDataSetChanged();
            }
        }
    }
}
