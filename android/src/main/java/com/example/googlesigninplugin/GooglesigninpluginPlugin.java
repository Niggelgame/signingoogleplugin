package com.example.googlesigninplugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ConditionVariable;

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
import io.flutter.plugin.common.EventChannel;
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
  //private final MethodChannel channel;
  GoogleSignInClient mGoogleSignInClient;
  private Result resultI;
  private MethodChannel methodChannel;
  private BinaryMessenger msg;
  private EventChannel eventChannel;
  BasicMessageChannel<String> channel;
  static Intent dataIntent;
  static final ConditionVariable gate = new ConditionVariable();

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    //flutterPluginBinding.getFlutterEngine().
    onAttachedToEngine(flutterPluginBinding.getApplicationContext(), flutterPluginBinding.getBinaryMessenger());
    //final MethodChannel channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "googlesigninplugin");
    //this.context = flutterPluginBinding.getApplicationContext();
    //channel.setMethodCallHandler(new GooglesigninpluginPlugin());
  }


  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
  }

  private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
    this.applicationContext = applicationContext;
    methodChannel = new MethodChannel(messenger, "googlesigninplugin");
    //eventChannel = new EventChannel(messenger, "googlesigninplugin");
    //eventChannel.setStreamHandler(this);
    channel = new BasicMessageChannel<String>(messenger, "foo", StringCodec.INSTANCE);
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
    registrar.addActivityResultListener(plugin);
    plugin.onAttachedToEngine(registrar.activeContext(), registrar.messenger());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if(call.method.equals("signIn")) {
      String client_id = call.argument("clientID");
      // Configure sign-in to request the user's ID, email address, and basic
      // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
      GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestIdToken(client_id)
              .requestEmail()
              .build();
      mGoogleSignInClient = GoogleSignIn.getClient(applicationContext, gso);
      this.resultI = result;
      channel.send("yallah");
      signIn();
    } else {
      result.notImplemented();
    }
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    //super.onActivityResult(requestCode, resultCode, data);
    channel.send("Should have logged");
    Log.w("FLUTTTTTTAAA", "WARRRRRUUUUUUMMMM");
    //resultI.success("RUN BABY");
    // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
    //if (requestCode == RC_SIGN_IN) {
      // The Task returned from this call is always completed, no need to attach
      // a listener.
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
    channel.send("Should have worked");
    resultI.success("12345");
      //handleSignInResult(task);
    //}
    return true;
  }

  private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
    channel.send("Running");
    try {
      GoogleSignInAccount account = completedTask.getResult(ApiException.class);

      channel.send("Past get result");


      assert account != null;
      channel.send("Past assertion");
      channel.send(account.getIdToken());
      Log.w(TAG, "signInResult " + account.getIdToken());
      resultI.success(account.getIdToken());
      // Signed in successfully, show authenticated UI.
      //updateUI(account);
    } catch (ApiException e) {
      channel.send("Past exception " + e.getStatusCode());
      // The ApiException status code indicates the detailed failure reason.
      // Please refer to the GoogleSignInStatusCodes class reference for more information.
      Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
      //updateUI(null);\
      resultI.success("");
    }
  }

  private void signIn() {
    //resultI.success("Run");
    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
    //Intent i = new Intent(applicationContext, ActivityHandler.class);
    //i.putExtra("intent", signInIntent);
    //this.startActivity(i);
    //Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(dataIntent);
    //handleSignInResult(task);
    activity.startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    this.activity = binding.getActivity();
    binding.addActivityResultListener(this);
    //context = binding.getActivity().getApplicationContext();
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

class ActivityHandler extends Activity {
  private boolean started = false;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent fr = getIntent();
    Intent toStartIntent = (Intent) fr.getSerializableExtra("intent");
    if (!started) {
      started = true;
      startActivityForResult(toStartIntent, 1);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    GooglesigninpluginPlugin.dataIntent = data;
    GooglesigninpluginPlugin.gate.open();
  }
}
