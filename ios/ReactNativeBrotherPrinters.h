
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNReactNativeBrotherPrintersSpec.h"

@interface ReactNativeBrotherPrinters : NSObject <NativeReactNativeBrotherPrintersSpec>
#else
#import <React/RCTBridgeModule.h>

@interface ReactNativeBrotherPrinters : NSObject <RCTBridgeModule>
#endif

@end
