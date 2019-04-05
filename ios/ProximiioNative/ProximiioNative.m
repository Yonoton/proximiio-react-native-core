//
//  ProximiioNative.m
//  ProximiioNative
//
//  Created by Matej Drzik on 03/04/2019.
//  Copyright © 2019 Proximi.io. All rights reserved.
//

#import "ProximiioNative.h"
#import <Proximiio/Proximiio.h>

@implementation ProximiioNative {
    bool hasListeners;
}

- (void)startObserving {
    hasListeners = true;
}

- (void)stopObserving {
    hasListeners = false;
}

- (NSArray<NSString *> *)supportedEvents {
    return @[
        @"ProximiioPositionUpdated",
        @"ProximiioHandleOutput",
        @"ProximiioHandlePush",
        @"ProximiioEnteredGeofence",
        @"ProximiioExitedGeofence",
        @"ProximiioFloorChanged",
        @"ProximiioFoundIBeacon",
        @"ProximiioUpdatedIBeacon",
        @"ProximiioLostIBeacon",
        @"ProximiioFoundEddystoneBeacon",
        @"ProximiioUpdatedEddystoneBeacon",
        @"ProximiioLostEddystoneBeacon",
        @"ProximiioEnteredPrivacyZone",
        @"ProximiioExitedPrivacyZone"
    ];
}

- (NSObject *)convertLocation:(ProximiioLocation *)location {
    NSObject *data = @{
      @"lat": @(location.coordinate.latitude),
      @"lng": @(location.coordinate.longitude)
    };

    if (location.horizontalAccuracy > 0) {
        [data setValue:@(location.horizontalAccuracy) forKey:@"accuracy"];
    }

    return data;
}

- (NSObject *)convertFloor:(ProximiioFloor *)floor {
    return @{
      @"id": floor.uuid,
      @"name": floor.name,
      @"level": floor.level,
      @"place_id": floor.placeId,
      @"floorplan": floor.floorPlanImageURL,
      @"anchors": floor.anchors
    };
}

- (NSObject *)convertGeofence:(ProximiioGeofence *)geofence {
    return @{
      @"id": geofence.uuid,
      @"name": geofence.name,
      @"area": [self convertLocation:geofence.area],
      @"radius": @(geofence.radius),
      @"isPolygon": @(geofence.isPolygon),
      @"polygon": geofence.polygon
    };
}

- (NSObject *)convertPrivacyZone:(ProximiioPrivacyZone *)privacyZone {
    return @{
      @"id": privacyZone.uuid,
      @"name": privacyZone.name,
      @"area": [self convertLocation:privacyZone.area],
      @"radius": @(privacyZone.radius),
      @"isPolygon": @(privacyZone.isPolygon),
      @"polygon": privacyZone.polygon
    };
}


- (NSObject *)convertInput:(ProximiioInput *)input {
    NSObject *data = @{
      @"id": input.uuid,
      @"name": input.name
    };

    if (input.type == kProximiioInputTypeIBeacon) {
        [data setValue:@"ibeacon" forKey:@"type"];
    } else if (input.type == kProximiioInputTypeEddystone) {
        [data setValue:@"eddystone" forKey:@"type"];
    } else {
        [data setValue:@"custom" forKey:@"type"];
    }

    return data;
}

- (NSObject *)convertIBeacon:(ProximiioIBeacon *)beacon {
    ProximiioInput *input = [[ProximiioResourceManager sharedManager] inputWithUUID:beacon.uuid
                                                                              major:beacon.major
                                                                              minor:beacon.minor];
    NSObject *data = @{
      @"uuid": beacon.uuid.UUIDString,
      @"major": @(beacon.major),
      @"minor": @(beacon.minor),
      @"accuracy": @(beacon.proximity)
    };

    if (input != nil) {
        [data setValue:[self convertInput:input] forKey:@"input"];
    }

    return data;
}

- (NSObject *)convertEddystoneBeacon:(ProximiioEddystoneBeacon *)beacon {
    ProximiioInput *input = [[ProximiioResourceManager sharedManager] inputWithNamespace:beacon.Namespace
                                                                                instance:beacon.InstanceID];
    NSObject *data = @{
      @"namespace": beacon.Namespace,
      @"instanceId": beacon.InstanceID
    };

    if (input != nil) {
        [data setValue:[self convertInput:input] forKey:@"input"];
    }

    return data;
}

- (void)proximiioHandleOutput:(NSObject *)payload {
    [self _sendEventWithName:@"ProximiioHandleOutput" body:payload];
}

- (void)proximiioPositionUpdated:(ProximiioLocation *)location {
    [self _sendEventWithName:@"ProximiioPositionUpdated" body:[self convertLocation:location]];
}

- (void)proximiioEnteredGeofence:(ProximiioGeofence *)geofence {
    [self _sendEventWithName:@"ProximiioEnteredGeofence" body:[self convertGeofence:geofence]];
}

- (void)proximiioExitedGeofence:(ProximiioGeofence *)geofence {
    [self _sendEventWithName:@"ProximiioExitedGeofence" body:[self convertGeofence:geofence]];
}

- (void)proximiioFloorChanged:(ProximiioFloor *)floor {
    [self _sendEventWithName:@"ProximiioFloorChanged" body:[self convertFloor:floor]];
}

- (void)proximiioFoundiBeacon:(ProximiioIBeacon *)beacon {
    [self _sendEventWithName:@"ProximiioFoundIBeacon" body:[self convertIBeacon:beacon]];
}

- (void)proximiioUpdatediBeacon:(ProximiioIBeacon *)beacon {
    [self _sendEventWithName:@"ProximiioUpdatedIBeacon" body:[self convertIBeacon:beacon]];
}

- (void)proximiioLostiBeacon:(ProximiioIBeacon *)beacon {
    [self _sendEventWithName:@"ProximiioLostIBeacon" body:[self convertIBeacon:beacon]];
}

- (void)proximiioFoundEddystoneBeacon:(ProximiioEddystoneBeacon *)beacon {
    [self _sendEventWithName:@"ProximiioFoundEddystoneBeacon" body:[self convertEddystoneBeacon:beacon]];
}

- (void)proximiioUpdatedEddystoneBeacon:(ProximiioEddystoneBeacon *)beacon {
    [self _sendEventWithName:@"ProximiioUpdatedEddystoneBeacon" body:[self convertEddystoneBeacon:beacon]];
}

- (void)proximiioLostEddystoneBeacon:(ProximiioEddystoneBeacon *)beacon {
    [self _sendEventWithName:@"ProximiioLostEddystoneBeacon" body:[self convertEddystoneBeacon:beacon]];
}

- (void)proximiioEnteredPrivacyZone:(ProximiioPrivacyZone *)privacyZone {
    [self _sendEventWithName:@"ProximiioEnteredPrivacyZone" body:[self convertPrivacyZone:privacyZone]];
}

- (void)proximiioExitedPrivacyZone:(ProximiioPrivacyZone *)privacyZone {
    [self _sendEventWithName:@"ProximiioExitedPrivacyZone" body:[self convertPrivacyZone:privacyZone]];
}

- (void)_sendEventWithName:(NSString *)event body:(id)body {
    if (hasListeners) {
        [self _sendEventWithName:event body:body];
    }
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(authWithToken:(NSString *)token
      authWithTokenwithResolver:(RCTPromiseResolveBlock)resolve
                       rejecter:(RCTPromiseRejectBlock)reject) {
    [Proximiio sharedInstance].delegate = self;
    [[Proximiio sharedInstance] authWithToken:token
                                     callback:^(ProximiioState result) {
                                         if (result == kProximiioReady) {
                                             resolve(@{@"visitorId": [Proximiio sharedInstance].visitorId });
                                         } else {
                                             NSError *error = [[NSError alloc] initWithDomain:NSURLErrorDomain
                                                                                        code:403
                                                                                    userInfo:nil];
                                             reject(@"403", @"Proximi.io authorization failed", error);
                                         }
                                     }];
}

RCT_EXPORT_METHOD(requestPermissions) {
    [[Proximiio sharedInstance] requestPermissions];
}

RCT_EXPORT_METHOD(visitorId:(RCTPromiseResolveBlock)resolve
                   rejecter:(RCTPromiseRejectBlock)reject) {
    resolve([Proximiio sharedInstance].visitorId);
}

RCT_EXPORT_METHOD(currentFloor:(RCTPromiseResolveBlock)resolve
                      rejecter:(RCTPromiseRejectBlock)reject) {
    resolve([self convertFloor:[Proximiio sharedInstance].currentFloor]);
}
@end
