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

  setBufferSize(buffer) {
    NativeModules.ProximiioNative.setBufferSize(buffer.id)
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
      UpdatedIBeacon: "ProximiioUpdatedIBeacon",
      LostIBeacon: "ProximiioLostIBeacon",
      FoundEddystoneBeacon: "ProximiioFoundEddystoneBeacon",
      UpdatedEddystoneBeacon: "ProximiioUpdatedEddystoneBeacon",
      LostEddystoneBeacon: "ProximiioLostEddystoneBeacon"
    }
  }

  get bufferSizes() {
    return [
      { id: 0, label: 'Mini 0.5s' },
      { id: 1, label: 'Small 1.2s' },
      { id: 2, label: 'Medium 3s' },
      { id: 3, label: 'Large 6s' },
      { id: 4, label: 'XLarge 10s' }
    ]
  }
}

const instance = new Proximiio()
module.exports = instance
