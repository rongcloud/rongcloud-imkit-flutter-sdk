package io.rong.flutter.imlib;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.rong.imlib.ChannelClient;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.IRongCoreListener;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UltraGroupTypingStatusInfo;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.VoiceMessage;

public class RCUltraGroupClient implements IRongCoreListener.UltraGroupMessageChangeListener, IRongCoreListener.UltraGroupReadTimeListener, IRongCoreListener.UltraGroupTypingStatusListener, IRongCoreListener.UltraGroupConversationListener {

    private static final String TAG = "RCUltraGroupClient";
    private static MethodChannel mChannel = null;
    private Handler mMainHandler = null;


    private static class SingletonHolder {
        private static final RCUltraGroupClient INSTANCE = new RCUltraGroupClient();
    }

    private RCUltraGroupClient() {
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public static RCUltraGroupClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void saveChannel(MethodChannel channel) {
        mChannel = channel;
    }

    public void setUltraGroupListeners(){
        Log.d(TAG, "setUltraGroupListeners");
        ChannelClient.getInstance().setUltraGroupMessageChangeListener(this);
        ChannelClient.getInstance().setUltraGroupReadTimeListener(this);
        ChannelClient.getInstance().setUltraGroupTypingStatusListener(this);
        ChannelClient.getInstance().setUltraGroupConversationListener(this);
    }

    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        String method = call.method;
        Log.d(TAG, "onMethodCall: " + method);

        if (!(call.arguments instanceof Map)) {
            Log.d(TAG, "onMethodCall: 参数非法" + method);
            result.success(null);
            return;
        }

        HashMap<String,Object> arguments = (HashMap<String, Object>) call.arguments;

        if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupSyncReadStatus)) {
            syncUltraGroupReadStatus(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupGetConversationListForAllChannel)){
            getConversationListForAllChannel(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupGetUnreadMentionedCount)){
            getUltraGroupUnreadMentionedCount(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupModifyMessage)){
            modifyUltraGroupMessage(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupRecallMessage)){
            recallUltraGroupMessage(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupDeleteMessages)){
            deleteUltraGroupMessages(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupSendTypingStatus)){
            sendUltraGroupTypingStatus(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupDeleteMessagesForAllChannel)){
            deleteUltraGroupMessagesForAllChannel(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupDeleteRemoteMessages)){
            deleteRemoteUltraGroupMessages(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupGetBatchRemoteMessages)){
            getBatchRemoteUltraGroupMessages(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupUpdateMessageExpansion)){
            updateUltraGroupMessageExpansion(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupGetNotificationQuietHoursLevel)){
            getNotificationQuietHoursLevel(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupSetConversationChannelNotificationLevel)){
            setConversationChannelNotificationLevel(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupSetNotificationQuietHoursLevel)){
            setNotificationQuietHoursLevel(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupGetConversationChannelNotificationLevel)){
            getConversationChannelNotificationLevel(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupSetConversationTypeNotificationLevel)){
            setConversationTypeNotificationLevel(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupGetConversationTypeNotificationLevel)){
            getConversationTypeNotificationLevel(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupSetConversationDefaultNotificationLevel)){
            setConversationDefaultNotificationLevel(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupGetConversationDefaultNotificationLevel)){
            getConversationDefaultNotificationLevel(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupSetConversationChannelDefaultNotificationLevel)){
            setConversationChannelDefaultNotificationLevel(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupGetConversationChannelDefaultNotificationLevel)){
            getConversationChannelDefaultNotificationLevel(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupGetUltraGroupUnreadCount)){
            getUltraGroupUnreadCount(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupGetUltraGroupAllUnreadCount)){
            getUltraGroupAllUnreadCount(arguments,result);
        } else if (method.equalsIgnoreCase(RCMethodList.RCUltraGroupGetUltraGroupAllUnreadMentionedCount)){
            getUltraGroupAllUnreadMentionedCount(arguments,result);
        }
    }

    private void getUltraGroupAllUnreadMentionedCount(HashMap<String, Object> arguments, final MethodChannel.Result result) {
        Log.d(TAG, "getUltraGroupAllUnreadMentionedCount: " + arguments);
        ChannelClient.getInstance().getUltraGroupAllUnreadMentionedCount(new IRongCoreCallback.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                msgMap.put("count",integer);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", e.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void getUltraGroupAllUnreadCount(HashMap<String, Object> arguments, final MethodChannel.Result result) {
        Log.d(TAG, "getUltraGroupAllUnreadCount: " + arguments);

        ChannelClient.getInstance().getUltraGroupAllUnreadCount(new IRongCoreCallback.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                msgMap.put("count",integer);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", e.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void getUltraGroupUnreadCount(HashMap<String, Object> arguments, final MethodChannel.Result result) {
        Log.d(TAG, "getUltraGroupUnreadCount: " + arguments);
        String targetId = (String) arguments.get("targetId");
        ChannelClient.getInstance().getUltraGroupUnreadCount(targetId, new IRongCoreCallback.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                msgMap.put("count",integer);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", e.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void getConversationChannelDefaultNotificationLevel(HashMap<String, Object> arguments, final MethodChannel.Result result) {
        Log.d(TAG, "getConversationChannelDefaultNotificationLevel: " + arguments);
        String targetId = (String) arguments.get("targetId");
        String channelId = (String) arguments.get("channelId");
        ChannelClient.getInstance().getUltraGroupConversationChannelDefaultNotificationLevel(targetId, channelId, new IRongCoreCallback.ResultCallback<IRongCoreEnum.PushNotificationLevel>() {
            @Override
            public void onSuccess(IRongCoreEnum.PushNotificationLevel pushNotificationLevel) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                msgMap.put("pushNotificationLevel",pushNotificationLevel.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", e.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void setConversationChannelDefaultNotificationLevel(HashMap<String, Object> arguments, final MethodChannel.Result result) {
        Log.d(TAG, "setConversationChannelDefaultNotificationLevel: " + arguments);
        String targetId = (String) arguments.get("targetId");
        String channelId = (String) arguments.get("channelId");
        Integer l = (Integer) arguments.get("pushNotificationLevel");
        IRongCoreEnum.PushNotificationLevel pushNotificationLevel = IRongCoreEnum.PushNotificationLevel.setValue(l);
        ChannelClient.getInstance().setUltraGroupConversationChannelDefaultNotificationLevel(targetId, channelId, pushNotificationLevel, new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                Log.d(TAG, "setConversationChannelDefaultNotificationLevel: onError " + coreErrorCode.getValue());
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", coreErrorCode.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void getConversationDefaultNotificationLevel(HashMap<String, Object> arguments, final MethodChannel.Result result) {
        Log.d(TAG, "getConversationDefaultNotificationLevel: " + arguments);
        String targetId = (String) arguments.get("targetId");
        ChannelClient.getInstance().getUltraGroupConversationDefaultNotificationLevel(targetId, new IRongCoreCallback.ResultCallback<IRongCoreEnum.PushNotificationLevel>() {
            @Override
            public void onSuccess(IRongCoreEnum.PushNotificationLevel pushNotificationLevel) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                msgMap.put("pushNotificationLevel",pushNotificationLevel.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", e.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void setConversationDefaultNotificationLevel(HashMap<String, Object> arguments, final MethodChannel.Result result) {
        Log.d(TAG, "setNotificationQuietHoursLevel: " + arguments);
        String targetId = (String) arguments.get("targetId");
        Integer l = (Integer) arguments.get("pushNotificationLevel");
        IRongCoreEnum.PushNotificationLevel pushNotificationLevel = IRongCoreEnum.PushNotificationLevel.setValue(l);

        ChannelClient.getInstance().setUltraGroupConversationDefaultNotificationLevel(targetId, pushNotificationLevel, new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", coreErrorCode.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void getConversationTypeNotificationLevel(HashMap<String, Object> arguments, final MethodChannel.Result result) {
        Log.d(TAG, "getConversationTypeNotificationLevel: " + arguments);
        Integer t = (Integer) arguments.get("conversationType");
        Conversation.ConversationType type = Conversation.ConversationType.setValue(t.intValue());
        // ChannelClient.getInstance().getConversationTypeNotificationLevel(type, new IRongCoreCallback.ResultCallback<IRongCoreEnum.PushNotificationLevel>() {
        //     @Override
        //     public void onSuccess(IRongCoreEnum.PushNotificationLevel pushNotificationLevel) {
        //         final HashMap<String,Object> msgMap = new HashMap<>();
        //         msgMap.put("code", 0);
        //         msgMap.put("pushNotificationLevel",pushNotificationLevel.getValue());
        //         mMainHandler.post(new Runnable() {
        //             @Override
        //             public void run() {
        //                 result.success(msgMap);
        //             }
        //         });
        //     }

        //     @Override
        //     public void onError(IRongCoreEnum.CoreErrorCode e) {
        //         final HashMap<String,Object> msgMap = new HashMap<>();
        //         msgMap.put("code", e.getValue());
        //         mMainHandler.post(new Runnable() {
        //             @Override
        //             public void run() {
        //                 result.success(msgMap);
        //             }
        //         });
        //     }
        // });
    }

    private void setConversationTypeNotificationLevel(HashMap<String, Object> arguments, final MethodChannel.Result result) {
        Log.d(TAG, "setConversationTypeNotificationLevel: " + arguments);
        Integer t = (Integer) arguments.get("conversationType");
        Conversation.ConversationType type = Conversation.ConversationType.setValue(t.intValue());
        Integer l = (Integer) arguments.get("pushNotificationLevel");
        IRongCoreEnum.PushNotificationLevel pushNotificationLevel = IRongCoreEnum.PushNotificationLevel.setValue(l);
        // ChannelClient.getInstance().setConversationTypeNotificationLevel(type, pushNotificationLevel, new IRongCoreCallback.OperationCallback() {
        //     @Override
        //     public void onSuccess() {
        //         final HashMap<String,Object> msgMap = new HashMap<>();
        //         msgMap.put("code", 0);
        //         mMainHandler.post(new Runnable() {
        //             @Override
        //             public void run() {
        //                 result.success(msgMap);
        //             }
        //         });
        //     }

        //     @Override
        //     public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
        //         final HashMap<String,Object> msgMap = new HashMap<>();
        //         msgMap.put("code", coreErrorCode.getValue());
        //         mMainHandler.post(new Runnable() {
        //             @Override
        //             public void run() {
        //                 result.success(msgMap);
        //             }
        //         });
        //     }
        // });
    }

    private void getConversationChannelNotificationLevel(HashMap<String, Object> arguments, final MethodChannel.Result result) {
        Log.d(TAG, "getConversationChannelNotificationLevel: " + arguments);
        Integer t = (Integer) arguments.get("conversationType");
        Conversation.ConversationType type = Conversation.ConversationType.setValue(t.intValue());
        String targetId = (String) arguments.get("targetId");
        String channelId = (String) arguments.get("channelId");

        ChannelClient.getInstance().getConversationChannelNotificationLevel(type, targetId, channelId, new IRongCoreCallback.ResultCallback<IRongCoreEnum.PushNotificationLevel>() {
            @Override
            public void onSuccess(IRongCoreEnum.PushNotificationLevel pushNotificationLevel) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                msgMap.put("pushNotificationLevel",pushNotificationLevel.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", e.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void setNotificationQuietHoursLevel(HashMap<String, Object> arguments, final MethodChannel.Result result) {
        Log.d(TAG, "setNotificationQuietHoursLevel: " + arguments);
        String startTime = (String) arguments.get("startTime");
        Integer spanMins = (Integer) arguments.get("spanMins");
        Integer l = (Integer) arguments.get("pushNotificationQuietHoursLevel");
        IRongCoreEnum.PushNotificationQuietHoursLevel pushNotificationQuietHoursLevel = IRongCoreEnum.PushNotificationQuietHoursLevel.setValue(l);
        ChannelClient.getInstance().setNotificationQuietHoursLevel(startTime, spanMins, pushNotificationQuietHoursLevel, new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", coreErrorCode.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void setConversationChannelNotificationLevel(HashMap<String, Object> arguments, final MethodChannel.Result result) {
        Log.d(TAG, "setConversationChannelNotificationLevel: " + arguments);
        String targetId = (String) arguments.get("targetId");
        String channelId = (String) arguments.get("channelId");
        Integer t = (Integer) arguments.get("conversationType");
        Conversation.ConversationType type = Conversation.ConversationType.setValue(t.intValue());
        Integer l = (Integer) arguments.get("pushNotificationLevel");
        IRongCoreEnum.PushNotificationLevel pushNotificationLevel = IRongCoreEnum.PushNotificationLevel.setValue(l);

        ChannelClient.getInstance().setConversationChannelNotificationLevel(type, targetId, channelId, pushNotificationLevel, new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", coreErrorCode.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void getNotificationQuietHoursLevel(HashMap<String, Object> arguments, final MethodChannel.Result result) {
        Log.d(TAG, "getNotificationQuietHoursLevel: " + arguments);
        String targetId = (String) arguments.get("targetId");
        Integer t = (Integer) arguments.get("conversationType");
        Conversation.ConversationType type = Conversation.ConversationType.setValue(t.intValue());

        ChannelClient.getInstance().getNotificationQuietHoursLevel(new IRongCoreCallback.GetNotificationQuietHoursCallbackEx() {
            @Override
            public void onSuccess(String startTime, int spanMinutes, IRongCoreEnum.PushNotificationQuietHoursLevel level) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                msgMap.put("startTime", startTime);
                msgMap.put("spanMinutes", spanMinutes);
                msgMap.put("pushNotificationQuietHoursLevel", level.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", coreErrorCode.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void syncUltraGroupReadStatus(HashMap<String,Object> arguments, final MethodChannel.Result result) {
        Log.d(TAG, "syncUltraGroupReadStatus: " + arguments);
        String targetId = (String) arguments.get("targetId");
        String channelId = (String) arguments.get("channelId");
        Number timestamp = (Number) arguments.get("timestamp");
        assert timestamp != null;
        ChannelClient.getInstance().syncUltraGroupReadStatus(targetId, channelId, timestamp.longValue(), new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "syncUltraGroupReadStatus: onSuccess");
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                Log.d(TAG, "syncUltraGroupReadStatus: onError " + coreErrorCode.getValue());
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", coreErrorCode.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });

    }

    private void getConversationListForAllChannel(HashMap<String,Object> arguments,final MethodChannel.Result result) {
        Log.d(TAG, "getConversationListForAllChannel: " + arguments);
        String targetId = (String) arguments.get("targetId");
        Integer t = (Integer) arguments.get("conversationType");
        Conversation.ConversationType type = Conversation.ConversationType.setValue(t.intValue());

        ChannelClient.getInstance().getConversationListForAllChannel(type, targetId, new IRongCoreCallback.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                final List<String> l = new ArrayList();
                if (conversations != null) {
                    for (Conversation con : conversations) {
                        String conStr = MessageFactory.getInstance().conversation2String(con);
                        l.add(conStr);
                    }
                }
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(l);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(null);
                    }
                });
            }
        });
    }

    private void getUltraGroupUnreadMentionedCount(HashMap<String,Object> arguments,final MethodChannel.Result result) {
        Log.d(TAG, "getUltraGroupUnreadMentionedCount: " + arguments);
        String targetId = (String) arguments.get("targetId");
        ChannelClient.getInstance().getUltraGroupUnreadMentionedCount(targetId, new IRongCoreCallback.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                msgMap.put("count",integer);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", e.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void modifyUltraGroupMessage(HashMap<String,Object> arguments,final MethodChannel.Result result) {

        Log.d(TAG, "modifyUltraGroupMessage: " + arguments);
        String messageUId = (String) arguments.get("messageUId");
        String contentStr = (String) arguments.get("content");
        String objectName = (String) arguments.get("objectName");
        byte[] bytes = contentStr.getBytes();

        MessageContent content = null;
        if (objectName.equalsIgnoreCase("RC:VcMsg")) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(contentStr);
                String localPath = jsonObject.getString("localPath");
                int duration = jsonObject.getInt("duration");
                Uri uri = Uri.parse(localPath);
                content = VoiceMessage.obtain(uri, duration);
            } catch (JSONException e) {
                // do nothing
            }
        } else {
            if (objectName.equalsIgnoreCase("RC:ReferenceMsg")) {
                content = RCIMFlutterWrapper.getInstance().makeReferenceMessage(contentStr);
            } else {
                content = RCIMFlutterWrapper.getInstance().newMessageContent(objectName, bytes, contentStr);
            }
        }
        // 处理引用消息内容丢失的问题

        if (content == null) {
//            RCLog.e(LOG_TAG + " message content is nil");
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    result.success(null);
                }
            });
            return;
        }
        ChannelClient.getInstance().modifyUltraGroupMessage(messageUId, content, new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", coreErrorCode.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }



    private void recallUltraGroupMessage(HashMap<String,Object> arguments,final MethodChannel.Result result) {

        Log.d(TAG, "recallUltraGroupMessage: " + arguments);
        final String messageUId = (String) arguments.get("messageUId");
        ChannelClient.getInstance().getMessageByUid(messageUId, new IRongCoreCallback.ResultCallback<Message>() {
            @Override
            public void onSuccess(final Message message) {
                ChannelClient.getInstance().recallUltraGroupMessage(message, new IRongCoreCallback.ResultCallback<RecallNotificationMessage>() {
                    @Override
                    public void onSuccess(RecallNotificationMessage recallNotificationMessage) {
                        message.setContent(recallNotificationMessage);
                        message.setObjectName("RC:RcNtf");
                        Map map = MessageFactory.getInstance().messageToMap(message);
                        final HashMap<String,Object> msgMap = new HashMap<>();
                        msgMap.put("code", 0);
                        msgMap.put("message",map);
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                result.success(msgMap);
                            }
                        });
                    }

                    @Override
                    public void onError(IRongCoreEnum.CoreErrorCode e) {
                        final HashMap<String,Object> msgMap = new HashMap<>();
                        msgMap.put("code", e.getValue());
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                result.success(msgMap);
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", e.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });

    }

    private void deleteUltraGroupMessages(HashMap<String,Object> arguments,final MethodChannel.Result result) {

        Log.d(TAG, "deleteUltraGroupMessages: " + arguments);
        String targetId = (String) arguments.get("targetId");
        String channelId = (String) arguments.get("channelId");
        Number timestamp = (Number) arguments.get("timestamp");
        ChannelClient.getInstance().deleteUltraGroupMessages(targetId, channelId, timestamp.longValue(), new IRongCoreCallback.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", e.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void sendUltraGroupTypingStatus(HashMap<String,Object> arguments,final MethodChannel.Result result) {

        Log.d(TAG, "sendUltraGroupTypingStatus: " + arguments);
        String targetId = (String) arguments.get("targetId");
        String channelId = (String) arguments.get("channelId");
        Integer t = (Integer) arguments.get("typingStatus");
        IRongCoreEnum.UltraGroupTypingStatus typingStatus = IRongCoreEnum.UltraGroupTypingStatus.valueOf(t);
        ChannelClient.getInstance().sendUltraGroupTypingStatus(targetId, channelId, typingStatus, new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", coreErrorCode.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void deleteUltraGroupMessagesForAllChannel(HashMap<String,Object> arguments,final MethodChannel.Result result) {

        Log.d(TAG, "deleteUltraGroupMessagesForAllChannel: " + arguments);
        String targetId = (String) arguments.get("targetId");
        Number timestamp = (Number) arguments.get("timestamp");
        ChannelClient.getInstance().deleteUltraGroupMessagesForAllChannel(targetId, timestamp.longValue(), new IRongCoreCallback.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });

            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", e.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void deleteRemoteUltraGroupMessages(HashMap<String,Object> arguments,final MethodChannel.Result result) {

        Log.d(TAG, "deleteRemoteUltraGroupMessages: " + arguments);
        String targetId = (String) arguments.get("targetId");
        String channelId = (String) arguments.get("channelId");
        Number timestamp = (Number) arguments.get("timestamp");
        ChannelClient.getInstance().deleteRemoteUltraGroupMessages(targetId, channelId, timestamp.longValue(), new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", coreErrorCode.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void getBatchRemoteUltraGroupMessages(HashMap<String,Object> arguments,final MethodChannel.Result result) {

        Log.d(TAG, "getBatchRemoteUltraGroupMessages: " + arguments);
        ArrayList list = (ArrayList) arguments.get("messages");
        if (list == null) {
            Log.d(TAG, "getBatchRemoteUltraGroupMessages: 参数非法");
            final HashMap<String,Object> msgMap = new HashMap<>();
            msgMap.put("code", 33003);
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    result.success(msgMap);
                }
            });
            return;
        }
        List<Message> messageList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            HashMap<String,HashMap> message = (HashMap<String, HashMap>) list.get(i);
            Message msg = RCIMFlutterWrapper.getInstance().map2Message(message);
            messageList.add(msg);
        }

        ChannelClient.getInstance().getBatchRemoteUltraGroupMessages(messageList, new IRongCoreCallback.IGetBatchRemoteUltraGroupMessageCallback() {
            @Override
            public void onSuccess(List<Message> matchedMsgList, List<Message> notMatchedMsgList) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                
                ArrayList<Map<String,Object>> arrayList = new ArrayList<>();

                if (matchedMsgList != null) {
                    for (Message message : matchedMsgList) {
                        Map map = MessageFactory.getInstance().messageToMap(message);
                        arrayList.add(map);
                    }
                }

                ArrayList<Map<String,Object>> notMatchedArrayResult = new ArrayList<>();

                if (notMatchedMsgList != null) {
                    for (Message message : notMatchedMsgList) {
                        Map map = MessageFactory.getInstance().messageToMap(message);
                        notMatchedArrayResult.add(map);
                    }
                }

                msgMap.put("code", 0);
                msgMap.put("matchedMsgList", arrayList);
                msgMap.put("notMatchMsgList", notMatchedArrayResult);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode errorCode) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", errorCode.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void updateUltraGroupMessageExpansion(HashMap<String,Object> arguments,final MethodChannel.Result result) {

        Log.d(TAG, "updateUltraGroupMessageExpansion: " + arguments);
        HashMap<String,String> expansionDic = (HashMap<String, String>) arguments.get("expansionDic");
        String messageUId = (String) arguments.get("messageUId");
        ChannelClient.getInstance().updateUltraGroupMessageExpansion(expansionDic, messageUId, new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", coreErrorCode.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    private void removeUltraGroupMessageExpansion(HashMap<String,Object> arguments,final MethodChannel.Result result) {

        Log.d(TAG, "removeUltraGroupMessageExpansion: " + arguments);
        String messageUId = (String) arguments.get("messageUId");
        ArrayList<String> arrayList = (ArrayList<String>) arguments.get("keyArray");
        ChannelClient.getInstance().removeUltraGroupMessageExpansion(messageUId, arrayList, new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", 0);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                final HashMap<String,Object> msgMap = new HashMap<>();
                msgMap.put("code", coreErrorCode.getValue());
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.success(msgMap);
                    }
                });
            }
        });
    }

    @Override
    public void onUltraGroupMessageExpansionUpdated(List<Message> messages) {

        Log.d(TAG, "onUltraGroupMessageExpansionUpdated");
        if (messages == null) {
            return;
        }
        ArrayList<Map<String,Object>> arrayList = new ArrayList<>();
        for (Message message : messages) {
            Map map = MessageFactory.getInstance().messageToMap(message);
            arrayList.add(map);
        }

        final HashMap<String,ArrayList> arguments = new HashMap<>();
        arguments.put("messages", arrayList);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mChannel.invokeMethod(RCMethodList.RCUltraGroupOnMessageExpansionUpdated, arguments);
            }
        });
        
    }

    @Override
    public void onUltraGroupMessageModified(List<Message> messages) {
        Log.d(TAG, "onUltraGroupMessageModified");
        if (messages == null) {
            return;
        }
        ArrayList<Map<String,Object>> arrayList = new ArrayList<>();
        for (Message message : messages) {
            Map map = MessageFactory.getInstance().messageToMap(message);
            arrayList.add(map);
        }

        final HashMap<String,ArrayList> arguments = new HashMap<>();
        arguments.put("messages", arrayList);
        
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mChannel.invokeMethod(RCMethodList.RCUltraGroupOnMessageModified, arguments);
            }
        });
    }

    @Override
    public void onUltraGroupMessageRecalled(List<Message> messages) {
        Log.d(TAG, "onUltraGroupMessageRecalled");
        if (messages == null) {
            return;
        }
        ArrayList<Map<String,Object>> arrayList = new ArrayList<>();
        for (Message message : messages) {
            Map map = MessageFactory.getInstance().messageToMap(message);
            arrayList.add(map);
        }

        final HashMap<String,ArrayList> arguments = new HashMap<>();
        arguments.put("messages", arrayList);

        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mChannel.invokeMethod(RCMethodList.RCUltraGroupOnMessageRecalled, arguments);
            }
        });
    }

    @Override
    public void onUltraGroupReadTimeReceived(String targetId, String channelId, long time) {
        Log.d(TAG, "onUltraGroupReadTimeReceived");
        final HashMap<String,Object> arguments = new HashMap<>();
        arguments.put("targetId",targetId);
        arguments.put("channelId",channelId);
        arguments.put("readTime", (int) time);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mChannel.invokeMethod(RCMethodList.RCUltraGroupOnReadTimeReceived, arguments);
            }
        });
    }

    @Override
    public void onUltraGroupTypingStatusChanged(List<UltraGroupTypingStatusInfo> infoList) {
        Log.d(TAG, "onUltraGroupTypingStatusChanged: ");
        if (infoList == null) {
            return;
        }
        ArrayList<Map<String,Object>> arrayList = new ArrayList<>();
        for (UltraGroupTypingStatusInfo statusInfo : infoList) {
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("targetId",statusInfo.getTargetId());
            hashMap.put("channelId",statusInfo.getChannelId());
            hashMap.put("userId",statusInfo.getUserId());
            hashMap.put("userNumbers",statusInfo.getUserNums());
            hashMap.put("timestamp",(int) statusInfo.getTimestamp());
            int a = statusInfo.getStatus().getType();
            hashMap.put("status",a);
            arrayList.add(hashMap);
        }
        final HashMap<String,ArrayList> arguments = new HashMap<>();
        arguments.put("infoArr", arrayList);

        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mChannel.invokeMethod(RCMethodList.RCUltraGroupOnTypingStatusChanged, arguments);
            }
        });

    }

    @Override
    public void ultraGroupConversationListDidSync() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mChannel.invokeMethod(RCMethodList.RCUltraGroupConversationListDidSync, null);
            }
        });
    }

}

