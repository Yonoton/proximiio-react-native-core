import {
  NativeModules,
  NativeEventEmitter
} from "react-native"

class Proximiio {
  constructor() {
    this.emitter = new NativeEventEmitter(NativeModules.ProximiioNative)
  }

  async authorize(token) {
    return await NativeModules.ProximiioNative.authWithToken(token)
  }

  async currentFloor() {
    return await NativeModules.ProximiioNative.currentFloor()
  }

  requestPermissions() {
    NativeModules.ProximiioNative.requestPermissions()
  }

  subscribe(event, fn) {
    return this.emitter.addListener(event, fn)
  }

  get Events() {
    return {
      PositionUpdated: "ProximiioPositionUpdated",
      FloorChanged: "ProximiioFloorChanged",
      EnteredGeofence: "ProximiioEnteredGeofence",
      ExitedGeofence: "ProximiioExitedGeofence",
      EnteredPrivacyZone: "ProximiioEnteredPrivacyZone",
      ExitedPrivacyZone: "ProximiioExitedPrivacyZone",
      FoundIBeacon: "ProximiioFoundIBeacon",
      LostIBeacon: "ProximiioLostIBeacon",
      FoundEddystoneBeacon: "ProximiioFoundEddystoneBeacon",
      LostEddystoneBeacon: "ProximiioLostEddystoneBeacon"
    }
  }
}

const instance = new Proximiio()
module.exports = instance