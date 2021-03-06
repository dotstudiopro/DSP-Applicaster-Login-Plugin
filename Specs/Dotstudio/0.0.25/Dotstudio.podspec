Pod::Spec.new do |s|

    s.name             = "Dotstudio"
    s.version          = '0.0.25'
    s.summary          = "An Example of full screen plugin for Zapp iOS."
    s.description      = <<-DESC
    An Example of full screen plugin for Zapp iOS.
                         DESC
    s.homepage         = "https://github.com/dotstudiopro/DSP-Applicaster-Login-Plugin.git"
    s.license          = 'MIT'
    s.author           = { "Ketan Sakariya" => "ketan@dotstudiopro.com" }
    s.source           = { :git => "https://github.com/dotstudiopro/DSP-Applicaster-Login-Plugin.git", :tag => s.version.to_s }
  
    s.ios.deployment_target  = "9.2"
    s.platform     = :ios, '9.2'
    s.requires_arc = true
    s.swift_version = '5.0'

    s.ios.dependency 'Alamofire', '4.9.1'
    s.ios.dependency 'SimpleKeychain'
    s.ios.dependency 'Lock', '~> 2.15.0'
    s.ios.dependency 'SwiftyStoreKit', '~> 0.15'
   
    s.subspec 'Core' do |c|
      s.resources = []
      c.frameworks = 'UIKit'
      c.source_files = 'iOS/PluginClasses/*.{swift,h,m}','iOS/PluginClasses/objects/*.{swift,h,m}','iOS/PluginClasses/api/*.{swift,h,m}','iOS/PluginClasses/login/*.{swift,h,m}','iOS/PluginClasses/utility/*.{swift,h,m}','iOS/PluginClasses/subscription/*.{swift,h,m}'
      # c.source_files = 'iOS/PluginClasses/objects/*.{swift,h,m}'
      # c.source_files = 'iOS/PluginClasses/api/*.{swift,h,m}'
      # c.source_files = 'iOS/PluginClasses/login/*.{swift,h,m}'
      # c.source_files = 'iOS/PluginClasses/utility/*.{swift,h,m}'
      # c.source_files = 'iOS/PluginClasses/subscription/*.{swift,h,m}'
      c.resources = 'iOS/PluginClasses/subscription/*.{storyboard,xcassets}'
      c.dependency 'ZappPlugins'
      # c.dependency 'SwiftyStoreKit', '~> 0.15'
    end
                  
    s.xcconfig =  { 'CLANG_ALLOW_NON_MODULAR_INCLUDES_IN_FRAMEWORK_MODULES' => 'YES',
                    'ENABLE_BITCODE' => 'YES',
                    'OTHER_LDFLAGS' => '$(inherited)',
                    'FRAMEWORK_SEARCH_PATHS' => '$(inherited) "${PODS_ROOT}"/**',
                    'LIBRARY_SEARCH_PATHS' => '$(inherited) "${PODS_ROOT}"/**',
                    'SWIFT_VERSION' => '5.0'
                  }
                  
    s.default_subspec = 'Core'
                  
  end
  