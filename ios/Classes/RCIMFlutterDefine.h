//
//  RCIMFlutterDefine.h
//  RongCloud
//
//  Created by Sin on 2019/6/5.
//
static NSString *RCMethodKeyInit = @"init";
static NSString *RCMethodKeyConnect = @"connect";
static NSString *RCMethodKeyDisconnect = @"disconnect";
static NSString *RCMethodKeyConfig = @"config";
static NSString *RCMethodKeyPushToConversationList = @"pushToConversationList";
static NSString *RCMethodKeyPushToConversation = @"pushToConversation";
static NSString *RCMethodKeySendMessage = @"sendMessage";
static NSString *RCMethodKeyRefreshUserInfo = @"refreshUserInfo";
static NSString *RCMethodKeyJoinChatRoom = @"joinChatRoom";
static NSString *RCMethodKeyQuitChatRoom = @"quitChatRoom";

//callback iOS 通知 flutter
static NSString *RCMethodCallBackKeySendMessage = @"sendMessageCallBack";
static NSString *RCMethodCallBackKeyRefreshUserInfo = @"refreshUserInfoCallBack";
static NSString *RCMethodCallBackKeyReceiveMessage = @"receiveMessageCallBack";
static NSString *RCMethodCallBackKeyJoinChatRoom = @"joinChatRoomCallBack";
static NSString *RCMethodCallBackKeyQuitChatRoom = @"quitChatRoomCallBack";