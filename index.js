import {
  Platform,
  NativeModules,
  NativeEventEmitter
} from "react-native"

class Proximiio {
  constructor() {
    this.emitter = new NativeEventEmitter(NativeModules.ProximiioNative)
    this.state = {}
  }

  isAuthorized () {
    return typeof this.state.visitor_id !== 'undefined'
  }

  async authorize(token) {
    this.state = await NativeModules.ProximiioNative.authWithToken(token)
    return this.state
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

  setNotificationMode(mode) {
    if (Platform.OS === 'android') {
      NativeModules.ProximiioNative.setNotificationMode(mode)
    }
  }

  setNotificationTitle(title) {
    if (Platform.OS === 'android') {
      NativeModules.ProximiioNative.setNotificationTitle(title)
    }
  }

  setNotificationText(text) {
    if (Platform.OS === 'android') {
      NativeModules.ProximiioNative.setNotificationText(text)
    }
  }

  setNotificationIcon(icon) {
    if (Platform.OS === 'android') {
      NativeModules.ProximiioNative.setNotificationIcon(icon)
    }
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

  get BufferSizes() {
    return [
      { id: 0, label: 'Mini 0.5s' },
      { id: 1, label: 'Small 1.2s' },
      { id: 2, label: 'Medium 3s' },
      { id: 3, label: 'Large 6s' },
      { id: 4, label: 'XLarge 10s' }
    ]
  }

  get NotificationModes() {
    return {
      Disabled: 0,
      Enabled: 1,
      Required: 2
    }
  }
}

const instance = new Proximiio()
module.exports = instance
