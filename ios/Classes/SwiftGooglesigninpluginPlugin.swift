import Flutter
import UIKit
import GoogleSignIn

public class SwiftGooglesigninpluginPlugin: NSObject, FlutterPlugin, GIDSignInDelegate {
    var accountResult: FlutterResult?;
    
    public func sign(_ signIn: GIDSignIn!, didSignInFor user: GIDGoogleUser!, withError error: Error!) {
        if let error = error {
          if (error as NSError).code == GIDSignInErrorCode.hasNoAuthInKeychain.rawValue {
            print("The user has not signed in before or they have since signed out.")
          } else {
            print("\(error.localizedDescription)")
          }
          return
        }
        
        accountResult?(user.authentication.idToken)
    }

    init(test: String) {
        super.init()
        GIDSignIn.sharedInstance().delegate = self
    }
    
    
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "googlesigninplugin", binaryMessenger: registrar.messenger())
    
    let instance = SwiftGooglesigninpluginPlugin(test: "test")
    registrar.addMethodCallDelegate(instance, channel: channel)
  }
    


  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    if(call.method == "signIn"){
        let arguments = call.arguments as? NSDictionary
        let clientID = arguments?["clientIDiOS"] as? String
        accountResult = result

        GIDSignIn.sharedInstance().clientID = clientID
        
        let viewController: UIViewController = getUIViewControllerTop()
        
        GIDSignIn.sharedInstance()?.presentingViewController = viewController
        do {
            try GIDSignIn.sharedInstance().signIn()
        } catch let err as NSError {
            print("Failed...")
        }
    }
  }
    
    private func getUIViewControllerTop() -> UIViewController {
        return (UIApplication.shared.delegate?.window??.rootViewController)!
    }
    
    
    public func application(_ application: UIApplication,
                     open url: URL, sourceApplication: String, annotation: Any) -> Bool {
      return GIDSignIn.sharedInstance().handle(url)
    }
}
