require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = "proximiio-react-native-core"
  s.summary      = "React Native Proximiio Module"
  s.version      = package['version']
  s.authors      = "Proximiio"
  s.homepage     = "https://github.com/proximiio/proximiio-react-native-core"
  s.license      = "MIT"
  s.platform     = :ios, "8.0"
  s.source       = { :git => "https://github.com/proximiio/proximiio-react-native-core.git" }
  s.source_files = "ios/ProximiioNative/**/*.{h,m}"

  s.vendored_frameworks = 'ios/ProximiioNative/Proximiio.framework', 'ios/ProximiioNative/IndoorAtlas.framework'

  s.dependency 'React'
end
