//
//  ProximiioNative.h
//  ProximiioNative
//
//  Created by Proximi.io on 03/03/2019.
//  Copyright © 2019 Proximi.io. All rights reserved.
//

#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <Proximiio/Proximiio.h>

@interface ProximiioNative : RCTEventEmitter <RCTBridgeModule, ProximiioDelegate>
@end
