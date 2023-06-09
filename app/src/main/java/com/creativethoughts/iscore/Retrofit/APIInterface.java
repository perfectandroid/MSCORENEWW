package com.creativethoughts.iscore.Retrofit;



import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Anusree on 03-10-2019.
 */

public interface APIInterface {

    @POST("api/Image/CustomerImageDets")
    Call<String> getImage(@Body RequestBody body);

    @POST("api/AccountSummary/ResellerDetails")
    Call<String> getResellerDetails(@Body RequestBody body); ////done

    @POST("api/AccountSummary/MaintenanceMessage")
    Call<String> getMaintenanceMessage(@Body RequestBody body); ////done

    @POST("api/AccountSummary/AccountSummaryDetails")
    Call<String> getCustomerModules(@Body RequestBody body); ////done

    @POST("api/AccountSummary/AccountModuleDetailsListInfo")
    Call<String> getAccountSummary(@Body RequestBody body); ////done

    @POST("api/AccountSummary/StandingInstructionDetails")
    Call<String> getStandingInstruction(@Body RequestBody body); ////done

    @POST("api/AccountSummary/NoticePostingDetails")
    Call<String> getIntimations(@Body RequestBody body); ////done

    @POST("api/AccountSummary/DashBoardAssetsDataDetails")
    Call<String> getDashboard(@Body RequestBody body); ////done

    @POST("api/AccountSummary/DashBoardLaibilityDataDetails")
    Call<String> getDashboardLaibility(@Body RequestBody body); ////done

    @POST("api/AccountSummary/DashBoardDataSavingsBankDetails")
    Call<String> getDashboardSavingsbank(@Body RequestBody body); ////done

    @POST("api/AccountSummary/DashBoardDataPaymentAndReceiptDetails")
    Call<String> getDashboardpaymentrecept(@Body RequestBody body); ////done

    @POST("api/AccountSummary/BranchLocationDetails")
    Call<String> getBankLocation(@Body RequestBody body); ////done

    @POST("api/AccountSummary/LoanSlabDetails")
    Call<String>getloanslabdetails(@Body RequestBody body); ////done

    @POST("api/AccountSummary/BankBranchDetails")
    Call<String> getBranchDetail(@Body RequestBody body); ////done

    @POST("api/AccountSummary/CustomerBankDetails")
    Call<String> getBankbranchList(@Body RequestBody body); ////done

    @POST("api/AccountSummary/CustomerProfileDetails")
    Call<String> getProfile(@Body RequestBody body);////done

    @POST("api/AccountSummary/AccountDueDateDetails")
    Call<String> getDuedate(@Body RequestBody body);////done

    @POST("api/AccountSummary/BarcodeFormatDet")
    Call<String> getBardCodeData(@Body RequestBody body); ////done

    @POST("api/AccountSummary/LoanMiniStatement")
    Call<String> getLoanMinistatement(@Body RequestBody body); ////done

    @POST("api/AccountSummary/BarcodeAgainstCustomerAccountDets")
    Call<String>getAccountList(@Body RequestBody body); ////done


    @POST("api/Statement/StatementOfAccount")
    Call<String>getAccountstatement(@Body RequestBody body); ////done

    @POST("api/AccountSummary/CustomerLoanAndDepositDetails")
    Call<String>getCustomerLoanandDeposit(@Body RequestBody body); ////done

    @POST("api/AccountSummary/RechargeHistory")
    Call<String>getRechargeHistory(@Body RequestBody body); ////done

    @POST("api/AccountSummary/FundTransferLimit")
    Call<String>getFundTransferLimit(@Body RequestBody body); ////done

    @POST("api/AccountSummary/OtherFundTransferHistory")
    Call<String>getFundTransferHistory(@Body RequestBody body); ////done

    //   @Headers("Accept: application/json")
    @GET("recharge_plan")
    Call<String> getOfferList(@Query("uid") String uid, @Query("pin") String pin, @Query("operator") String poperator);

    @POST("api/Recharge/RechargeOffers")
    Call<String> getRechargeOffer(@Body RequestBody body);

    @POST("api/AccountSummary/BalanceSplitUpDetails")
    Call<String>getBalanceSplitUpDetails(@Body RequestBody body); ////done

    @POST("api/AccountSummary/OwnAccounDetails")
    Call<String>getOwnAccounDetails(@Body RequestBody body);

    @POST("api/AccountSummary/GetInstalmmentRemittanceAmount")
    Call<String>getGetInstalmmentRemittanceAmount(@Body RequestBody body); ////done

    @POST("api/AccountSummary/PassBookAccountDetails")
    Call<String>getPassBookAccountDetails(@Body RequestBody body); ////done

    @POST("api/AccountSummary/PassBookAccountStatement")
    Call<String>getPassBookAccountStatement(@Body RequestBody body); ////done

    @POST("api/AccountSummary/PassBookAccountTransactionList")
    Call<String>getPassBookAccountTransactionList(@Body RequestBody body);

    @POST("api/cbsMobile/CardBalance")
    Call<String>getCardBalance(@Body RequestBody body);

    @POST("api/cbsMobile/CardMiniStatement")
    Call<String>getCardMiniStatement(@Body RequestBody body);

    @POST("api/cbsMobile/CardTopUpandReverse")
    Call<String>getCardTopUpandReverse(@Body RequestBody body);

    @POST("api/Recharge/ProvidersList")
    Call<String>getProvidersList(@Body RequestBody body);

    @POST("api/Recharge/RechargeCircleDetails")
    Call<String>getRechargeCircleDetails(@Body RequestBody body);

    @POST("api/AccountSummary/FundTransferIntraBank")
    Call<String>getfundtransfrintrabnk(@Body RequestBody body);

    @POST("api/Recharge/MobileRecharge")
    Call<String>getMobileRecharge(@Body RequestBody body);

    @POST("api/Recharge/POSTPaidBilling")
    Call<String>getPOSTPaidBilling(@Body RequestBody body);

    @POST("api/Recharge/DTHRecharge")
    Call<String>getDTHRecharge(@Body RequestBody body);

    @POST("api/Recharge/KSEBSectionDetails")
    Call<String>getKSEBSectionDetails(@Body RequestBody body);

    @POST("api/Recharge/KSEBPaymentRequest")
    Call<String>getKSEBPaymentRequest(@Body RequestBody body);

    @POST("api/Customer/LoginVerification")
    Call<String>getPassBookAuthenticateNew(@Body RequestBody body);

    @POST("api/Customer/MessageDetails")
    Call<String>getMessageDetails(@Body RequestBody body);

    @POST("api/AccountSummary/NEFTRTGSGetReceiver")
    Call<String>getNeftReeceiverList(@Body RequestBody body);

    @POST("api/AccountSummary/NEFTRTGSDeleteReceiver")
    Call<String>getNeftdlterecvr(@Body RequestBody body);

    @POST("api/AccountSummary/NEFTRTGSPayment")
    Call<String>getNeftPaymnt(@Body RequestBody body);

    @POST("api/AccountSummary/MTAddnewsender")
    Call<String>getAddsender(@Body RequestBody body);

    @POST("api/AccountSummary/MTAddnewreceiver")
    Call<String>getAddReceiver(@Body RequestBody body);


    @POST("api/AccountSummary/GenerateSenderReceiverList")
    Call<String>getSenderList(@Body RequestBody body);

    @POST("api/Customer/CheckVersionCode")
    Call<String>getVersioncode(@Body RequestBody body);

    @POST("api/Customer/VerifyOTP")
    Call<String>getVerifyOTP(@Body RequestBody body);

    @POST("api/Recharge/KSEBTransactionResponse")
    Call<String>KSEBTransactionResponse(@Body RequestBody body);

    @POST("api/Customer/ChangeMpin")
    Call<String>doChangeMpin(@Body RequestBody body);

    @POST("api/AccountSummary/MoneyTransferPayment")
    Call<String>getMoneytransferPayment(@Body RequestBody body);

    @POST("api/Customer/MTVerifyPaymentOTP")
    Call<String>getVerifyPaymentOTP(@Body RequestBody body);

    @POST("api/Customer/MTVerifySenderOTP")
    Call<String>getVerifySenderOTP(@Body RequestBody body);

    @POST("api/Customer/MTVerifyReceiverOTP")
    Call<String>getVerifyReceiverOTP(@Body RequestBody body);

    @POST("api/AccountSummary/MTResendMPIN")
    Call<String>getForgotMpin(@Body RequestBody body);

    @POST("api/Recharge/KSEBCommission")
    Call<String>getKSEBCommission(@Body RequestBody body);

    @POST("api/Customer/MTResendSenderOTP")
    Call<String>getResendersenderotp(@Body RequestBody body);

    @POST("api/AccountSummary/RecieverModuleList")
    Call<String>getRecieverModuleList(@Body RequestBody body);

    @POST("api/AccountSummary/FundTransferIntraBankOtherAccLiabilityAmount")
    Call<String>getAccLiabilityAmount(@Body RequestBody body);

}
