Pod::Spec.new do |s|

    s.name             = "Dotstudio"
    s.version          = '0.0.10'
    s.summary          = "An Example of full screen plugin for Zapp iOS."
    s.description      = <<-DESC
    An Example of full screen plugin for Zapp iOS.
                         DESC
    s.homepage         = "https://github.com/dotstudiopro/DSP-Applicaster-Login-Plugin.git"
    s.license          = 'MIT'
    s.author           = { "Ketan Sakariya" => "ketan@dotstudiopro.com" }
    s.source           = { :git => "https://github.com/dotstudiopro/DSP-Applicaster-Login-Plugin.git", :tag => s.version.to_s }
  
    s.ios.deployment_target  = "10.0"
    s.platform     = :ios, '10.0'
    s.requires_arc = true
    s.swift_version = '5.0'

    s.ios.dependency 'Alamofire'
    s.ios.dependency 'SimpleKeychain'
    s.ios.dependency 'Lock', '~> 2.10'
   
    s.subspec 'Core' do |c|
      s.resources = []
      c.frameworks = 'UIKit'
      c.source_files = 'PluginClasses/*.{swift,h,m}','PluginClasses/**/*.{swift,h,m}'
      c.dependency 'ZappPlugins'
      c.dependency 'SwiftyStoreKit', '~> 0.15'
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
  