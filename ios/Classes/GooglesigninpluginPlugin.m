#import "GooglesigninpluginPlugin.h"
#if __has_include(<googlesigninplugin/googlesigninplugin-Swift.h>)
#import <googlesigninplugin/googlesigninplugin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "googlesigninplugin-Swift.h"
#endif

@implementation GooglesigninpluginPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftGooglesigninpluginPlugin registerWithRegistrar:registrar];
}
@end
