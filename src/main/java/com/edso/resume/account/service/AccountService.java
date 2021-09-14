package com.edso.resume.account.service;


import com.edso.resume.account.domain.request.*;
import com.edso.resume.account.entities.UserEntity;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;

public interface AccountService {

    GetArrayResponse<UserEntity> getAccounts(
            HeaderInfo headerInfo, String name,
            Integer role,
            Integer status,
            String dateOfBirth,
            Integer page,
            Integer size);

    BaseResponse createAccount(CreateAccountRequest request);

    BaseResponse updateAccount(UpdateAccountRequest request);

    BaseResponse deleteAccount(DeleteAccountRequest request);

    BaseResponse changePasswordAcc(ChangePasswordAccRequest request);

    BaseResponse changePasswordSelf(ChangePasswordSelfRequest request);

}
