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

    @POST("Mscore/api/Image/CustomerImageDets")
    Call<String> getImage(@Body RequestBody body);

    @POST("Mscore/api/AccountSummary/ResellerDetails")
    Call<String> getResellerDetails(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/MaintenanceMessage")
    Call<String> getMaintenanceMessage(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/AccountSummaryDetails")
    Call<String> getCustomerModules(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/AccountModuleDetailsListInfo")
    Call<String> getAccountSummary(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/StandingInstructionDetails")
    Call<String> getStandingInstruction(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/NoticePostingDetails")
    Call<String> getIntimations(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/DashBoardAssetsDataDetails")
    Call<String> getDashboard(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/DashBoardLaibilityDataDetails")
    Call<String> getDashboardLaibility(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/DashBoardDataSavingsBankDetails")
    Call<String> getDashboardSavingsbank(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/DashBoardDataPaymentAndReceiptDetails")
    Call<String> getDashboardpaymentrecept(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/BranchLocationDetails")
    Call<String> getBankLocation(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/LoanSlabDetails")
    Call<String>getloanslabdetails(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/BankBranchDetails")
    Call<String> getBranchDetail(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/CustomerBankDetails")
    Call<String> getBankbranchList(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/CustomerProfileDetails")
    Call<String> getProfile(@Body RequestBody body);////done

    @POST("Mscore/api/AccountSummary/AccountDueDateDetails")
    Call<String> getDuedate(@Body RequestBody body);////done

    @POST("Mscore/api/AccountSummary/BarcodeFormatDet")
    Call<String> getBardCodeData(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/LoanMiniStatement")
    Call<String> getLoanMinistatement(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/BarcodeAgainstCustomerAccountDets")
    Call<String>getAccountList(@Body RequestBody body); ////done


    @POST("Mscore/api/Statement/StatementOfAccount")
    Call<String>getAccountstatement(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/CustomerLoanAndDepositDetails")
    Call<String>getCustomerLoanandDeposit(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/RechargeHistory")
    Call<String>getRechargeHistory(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/FundTransferLimit")
    Call<String>getFundTransferLimit(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/OtherFundTransferHistory")
    Call<String>getFundTransferHistory(@Body RequestBody body); ////done

    //   @Headers("Accept: application/json")
    @GET("recharge_plan")
    Call<String> getOfferList(@Query("uid") String uid, @Query("pin") String pin, @Query("operator") String poperator);

    @POST("Mscore/api/Recharge/RechargeOffers")
    Call<String> getRechargeOffer(@Body RequestBody body);

    @POST("Mscore/api/AccountSummary/BalanceSplitUpDetails")
    Call<String>getBalanceSplitUpDetails(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/OwnAccounDetails")
    Call<String>getOwnAccounDetails(@Body RequestBody body);

    @POST("Mscore/api/AccountSummary/GetInstalmmentRemittanceAmount")
    Call<String>getGetInstalmmentRemittanceAmount(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/PassBookAccountDetails")
    Call<String>getPassBookAccountDetails(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/PassBookAccountStatement")
    Call<String>getPassBookAccountStatement(@Body RequestBody body); ////done

    @POST("Mscore/api/AccountSummary/PassBookAccountTransactionList")
    Call<String>getPassBookAccountTransactionList(@Body RequestBody body); ////done
}
