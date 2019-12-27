package com.killy.netspay;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import android.app.Activity;
import android.os.Bundle;

import com.nets.enets.exceptions.InvalidPaymentRequestException;
import com.nets.enets.listener.PaymentCallback;
import com.nets.enets.network.PaymentRequestManager;
import com.nets.enets.utils.result.DebitCreditPaymentResponse;
import com.nets.enets.utils.result.NETSError;
import com.nets.enets.utils.result.NonDebitCreditPaymentResponse;
import com.nets.enets.utils.result.PaymentResponse;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class NetspayModule extends ReactContextBaseJavaModule {

    private DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter;
    private final ReactApplicationContext reactContext;
    public NetspayModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
      }
    
        @Override
        public String getName() {
            return "Netspay";
        }
    
    public class NetsPayment extends AppCompatActivity {


        public void startPay(String apiKey, String hmac, String txn, final Promise promise){
            PaymentRequestManager manager = PaymentRequestManager.getSharedInstance();
            try {
                manager.sendPaymentRequest(apiKey,
                                           hmac,
                                           txn, new PaymentCallback() {
                                            @Override
                                            public void onResult(final PaymentResponse response) {
                                     
                                                /*
                                                 This sample codes check whether the instance belongs to DebitCreditPaymentResponse or NonDebitCreditPaymentResponse. It will extract the data accordingly.
                                                 If it is DebitCreditPaymentResponse object, response will contain the TXN response, HMAC value and Key ID.
                                                 If it is NonDepublic class MainActivity extends AppCompatActivity {bitCreditPaymentResponse object, the response comes from in-app communication and it will contain the status. In this case, it is NETS Pay.
                                                 */
                                                if (response instanceof DebitCreditPaymentResponse) {
                                                    final DebitCreditPaymentResponse debitCreditResponse = (DebitCreditPaymentResponse) response;
                                     
                                                    String txnRes    = debitCreditResponse.txnResp;
                                                    String hmac  = debitCreditResponse.hmac;
                                                    String keyId = debitCreditResponse.keyId;
                                                    promise.resolve(debitCreditResponse);
                                                } else if (response instanceof NonDebitCreditPaymentResponse) {
                                                    final NonDebitCreditPaymentResponse nonDebitCreditResponse =   (NonDebitCreditPaymentResponse) response;
                                     
                                                    String txn_Status      = nonDebitCreditResponse.status;
                                                    promise.resolve(response);
                                                 }
                                            }
                                     
                                            @Override
                                            public void onFailure(final NETSError error) {
                                                String txn_ResponseCode = error.responeCode;
                                                String txn_ActionCode   = error.actionCode;
                                                promise.reject(error.responeCode);
                                                   }
                                            },this);
            } catch (InvalidPaymentRequestException ipre){
                // Do something with the exception
                promise.reject("error ipre: "+ipre);
            } catch (Exception e){
                // Do something with the exception
                promise.reject("error e: "+e);
            }
        }
    }
    private String txnAmount;
    private String merchantTxnRef;
    private String b2sTxnEndURL;
    private String s2sTxnEndURL;
    private String netsMid;
    private String merchantTxnDtm;
    private String mobileOs;
    private String submissionMode;
    private String paymentType;
    private String paymentMode;
    private String clientType;
    private Boolean paramsValid = true;
    private String txn;
    private String hmac;
    private String apiKey;

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }

    @ReactMethod
    public void makePayment(ReadableMap options, Promise promise){
        paramsValid = true;

        if (options.hasKey("txn")) {
            txn = options.getString("txn");
        }else{
            paramsValid = false;
        }
        if (options.hasKey("hmac")) {
            hmac = options.getString("hmac");
        }else{
            paramsValid = false;
        }
        if (options.hasKey("apiKey")) {
            apiKey = options.getString("apiKey");
        }else{
            paramsValid = false;
        }
        if(paramsValid == false){
            promise.reject("Invalid Params Please refer to Documentation for valid params list we got txn: "+ txn + " hmac: " + hmac + " apikey: " + apiKey );
        }
        NetspayModule netspaymodule = new NetspayModule(reactContext);
        NetspayModule.NetsPayment netsPayment = netspaymodule.new NetsPayment();
        netsPayment.startPay(apiKey, hmac, txn, promise);

        }
    
}

