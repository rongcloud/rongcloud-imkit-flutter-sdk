#include "AppDelegate.h"
#include "GeneratedPluginRegistrant.h"
#import <RongIMLib/RongIMLib.h>
#import "RCDTestMessage.h"
#import <rongcloud_im_plugin/RCIMFlutterWrapper.h>

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    [GeneratedPluginRegistrant registerWithRegistry:self];
    // Override point for customization after application launch.
    // 注册自定义 MethodChannel，欲注册自定义消息，请查看 channel 具体实现
    [self addRongCloudCustomChannel];
    /**
     * 推送处理1 (申请推送权限)
     */
    if ([application
         respondsToSelector:@selector(registerUserNotificationSettings:)]) {
        //注册推送, 用于iOS8以及iOS8之后的系统
        UIUserNotificationSettings *settings = [UIUserNotificationSettings
                                                settingsForTypes:(UIUserNotificationTypeBadge |
                                                                  UIUserNotificationTypeSound |
                                                                  UIUserNotificationTypeAlert)
                                                categories:nil];
        [application registerUserNotificationSettings:settings];
    }
    
//    // 远程推送的内容
//    NSDictionary *remoteNotificationUserInfo = launchOptions[UIApplicationLaunchOptionsRemoteNotificationKey];
//
//    // 传递远程推送数据
//    if (remoteNotificationUserInfo != nil) {
//        //远程推送的数据，延时之后再调用该接口，防止Flutter尚未初始化就调用，导致Flutter无法接受数据
//        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
//            [[RCIMFlutterWrapper sharedWrapper] sendDataToFlutter:remoteNotificationUserInfo];
//        });
//    }
    return [super application:application didFinishLaunchingWithOptions:launchOptions];
}

/**
 * 推送处理2
 */
//注册用户通知设置
- (void)application:(UIApplication *)application didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings {
    // register to receive notifications
    [application registerForRemoteNotifications];
}

/**
 * 推送处理3
 */
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    NSString *token = [[[[deviceToken description] stringByReplacingOccurrencesOfString:@"<" withString:@""]
                        stringByReplacingOccurrencesOfString:@">"
                        withString:@""] stringByReplacingOccurrencesOfString:@" "
                       withString:@""];
    
    [[RCIMClient sharedRCIMClient] setDeviceToken:token];
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo{
    
//    [[RCIMFlutterWrapper sharedWrapper] sendDataToFlutter:userInfo];
}

- (void)addRongCloudCustomChannel {
    FlutterViewController* controller = (FlutterViewController*)self.window.rootViewController;
    FlutterMethodChannel *channel =
        [FlutterMethodChannel methodChannelWithName:@"com.example.rongcloud_im_plugin_example/exampleChannel"
                                    binaryMessenger:controller.binaryMessenger];
    [channel setMethodCallHandler:^(FlutterMethodCall* call, FlutterResult result) {
        if ([@"registerCustomMessages" isEqualToString:call.method]) {
            //注册自定义的消息。该方法紧跟 Flutter 侧的 RongIMClient.init 调用。
            [[RCIMClient sharedRCIMClient] registerMessageType:[RCDTestMessage class]];
            result(nil);
        } else {
            result(FlutterMethodNotImplemented);
        }
    }];
}

@end
