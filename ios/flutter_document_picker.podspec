#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'flutter_document_picker'
  s.version          = '0.0.1'
  s.summary          = 'A Flutter wrapper for Apple&#x27;s UIDocumentMenuViewController and for Android&#x27;s Intent.ACTION_OPEN_DOCUMENT &#x2F; Intent.ACTION_PICK.'
  s.description      = <<-DESC
A Flutter wrapper for Apple&#x27;s UIDocumentMenuViewController and for Android&#x27;s Intent.ACTION_OPEN_DOCUMENT &#x2F; Intent.ACTION_PICK.
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  
  s.ios.deployment_target = '8.0'
end

