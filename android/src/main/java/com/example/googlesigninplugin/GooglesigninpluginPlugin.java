package com.example.googlesigninplugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.StringCodec;

import static android.content.ContentValues.TAG;

/** GooglesigninpluginPlugin */
public class GooglesigninpluginPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {
  private static final int RC_SIGN_IN = 500;
  private Activity activity;
  private Context applicationContext;
  private GoogleSignInClient mGoogleSignInClient;
  private Result result;
  private MethodChannel methodChannel;
  private BasicMessageChannel<String> channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    onAttachedToEngine(flutterPluginBinding.getApplicationContext(), flutterPluginBinding.getBinaryMessenger());
  }

  public GooglesigninpluginPlugin(){
    Log.w("%", "Created instance of GoogleSigninPluginPlugin");
    Log.e("ClassCreation", "Frunning", new Exception());
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {

  }

  private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
    this.applicationContext = applicationContext;
    methodChannel = new MethodChannel(messenger, "googlesigninplugin");
    //eventChannel = new EventChannel(messenger, "googlesigninplugin");
    //eventChannel.setStreamHandler(this);
    channel = new BasicMessageChannel<>(messenger, "foo", StringCodec.INSTANCE);
    methodChannel.setMethodCallHandler(this);
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    GooglesigninpluginPlugin plugin = new GooglesigninpluginPlugin();
    System.out.println("registerwith:");
    System.out.println(plugin);
    registrar.addActivityResultListener(plugin);
    plugin.onAttachedToEngine(registrar.activeContext(), registrar.messenger());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    System.out.println(this);
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if(call.method.equals("signIn")) {
      String clientID = call.argument("clientIDAndroid");
      Log.w("%%%", clientID);
      // Configure sign-in to request the user's ID, email address, and basic
      // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
      GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestIdToken(clientID)
              .requestEmail()
              .build();
      mGoogleSignInClient = GoogleSignIn.getClient(applicationContext, gso);
      this.result = result;
      signIn();
    } else {
      result.notImplemented();
    }
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
    handleSignInResult(task);
    return true;
  }

  private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
    channel.send("Running");
    try {
      try {
        GoogleSignInAccount account = completedTask.getResult(ApiException.class);

        if (account == null) {
          this.result.error("shit", "fuck", null);
        } else {
          Log.w(TAG, "signInResult " + account.getIdToken());
          this.result.success(account.getIdToken());
        }
      } catch (ApiException e) {
        channel.send("Past exception " + e.getStatusCode());
        // The ApiException status code indicates the detailed failure reason.
        // Please refer to the GoogleSignInStatusCodes class reference for more information.
        //e.printStackTrace();
        Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        this.result.error(String.valueOf(e.getStatusCode()), e.getMessage(), e);
      }
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  private void signIn() {
    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
    activity.startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    System.out.println(this);
    this.activity = binding.getActivity();
    binding.addActivityResultListener(this);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {

  }
}

