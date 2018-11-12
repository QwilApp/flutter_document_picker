#import "FlutterDocumentPickerPlugin.h"

#define IDIOM    UI_USER_INTERFACE_IDIOM()
#define IPAD     UIUserInterfaceIdiomPad

@implementation FlutterDocumentPickerPlugin {
  FlutterViewController *_flutterViewController;
  FlutterResult _result;
}

+ (void)registerWithRegistrar:(NSObject <FlutterPluginRegistrar> *)registrar {
  FlutterViewController *controller = (FlutterViewController *) [UIApplication sharedApplication].delegate.window.rootViewController;
  FlutterMethodChannel *channel = [FlutterMethodChannel methodChannelWithName:@"flutter_document_picker" binaryMessenger:[registrar messenger]];
  FlutterDocumentPickerPlugin *instance = [[FlutterDocumentPickerPlugin alloc] initWith:controller];
  [registrar addMethodCallDelegate:instance channel:channel];
}


- (instancetype)initWith:(FlutterViewController *)controller {
  self = [super init];
  if (self) {
    _flutterViewController = controller;
  }

  return self;
}

- (void)handleMethodCall:(FlutterMethodCall *)call result:(FlutterResult)result {
  if ([@"show" isEqualToString:call.method]) {
    _result = result;
    NSString *fileType = call.arguments[@"fileType"];

    UIDocumentPickerViewController *documentPickerViewController = [[UIDocumentPickerViewController alloc] initWithDocumentTypes:@[fileType] inMode:UIDocumentPickerModeImport];

    documentPickerViewController.delegate = self;
    documentPickerViewController.modalPresentationStyle = UIModalPresentationFormSheet;

    if (IDIOM == IPAD) {
      [documentPickerViewController.popoverPresentationController setSourceView:_flutterViewController.view];
    }

    [_flutterViewController presentViewController:documentPickerViewController animated:YES completion:nil];
  } else {
    result(FlutterMethodNotImplemented);
  }
}

- (void)documentPicker:(UIDocumentPickerViewController *)controller didPickDocumentAtURL:(NSURL *)url {
  if (controller.documentPickerMode == UIDocumentPickerModeImport) {
    [url startAccessingSecurityScopedResource];

    NSFileCoordinator *coordinator = [[NSFileCoordinator alloc] init];
    __block NSError *error;

    [coordinator coordinateReadingItemAtURL:url options:NSFileCoordinatorReadingResolvesSymbolicLink error:&error byAccessor:^(NSURL *newURL) {
        NSMutableDictionary *result = [NSMutableDictionary dictionary];

        [result setValue:newURL.absoluteString forKey:@"path"];
        [result setValue:[newURL lastPathComponent] forKey:@"fileName"];

        NSError *attributesError = nil;
        NSDictionary *fileAttributes = [[NSFileManager defaultManager] attributesOfItemAtPath:newURL.path error:&attributesError];
        if (!attributesError) {
          [result setValue:fileAttributes[NSFileSize] forKey:@"fileSize"];
        } else {
          NSLog(@"%@", attributesError);
        }

        _result(result);
        _result = nil;
    }];

    [url stopAccessingSecurityScopedResource];
  }
}

@end
