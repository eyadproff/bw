    bw.startup:
        - services-gateway-lookups/api/application/configs/v1
        - services-gateway-lookups/api/calendar/hijri/v1
    
    bw.login:
        - services-gateway-identity/api/login/v1
        - services-gateway-identity/api/login/fingerprint/v2
        - services-gateway-identity/api/password/v1
        - services-gateway-lookups/api/application/menu-roles/v1
        - services-gateway-identity/api/token/refresh/v1
        - services-gateway-identity/api/logout/v1
        
    bw.coreServices:
        - services-gateway-demographic/api/activity/client/error/v1
    
    menu.cancel.cancelCriminal:
        - services-gateway-lookups/api/application/person-type/v1
        - services-gateway-biooperation/api/xafis/delink/person-id/v1
        - services-gateway-biooperation/api/xafis/delink/inquiry-id/v1
    
    menu.cancel.cancelLatent:
        - services-gateway-biooperation/api/latent/delink/v1
        
    menu.query.civilcriminalfingerprintsinquiry:
        - services-gateway-lookups/api/application/person-type/v1
        - services-gateway-lookups/api/application/id-types/v1
        - services-gateway-lookups/api/application/nationality/all/v1
        - services-gateway-lookups/api/application/crime-types/v1
        - services-gateway-lookups/api/bio-exchange/crimes/v1
        - services-gateway-lookups/api/bio-exchange/parties/v1
        - services-gateway-demographic/api/person/info/v1
        - services-gateway-biooperation/api/fingerprint/images/v1
        - services-gateway-biooperation/api/fingerprint/available/v1
        - services-gateway-biooperation/api/fingerprint/images/v2
        - services-gateway-biooperation/api/fingerprint/available/v2
        - services-gateway-biooperation/api/criminal/fingerprint/images/v1
        - services-gateway-biooperation/api/xafis/nist/extraction/v1
        - services-gateway-biooperation/api/fingerprint/inquiry/v1
        - services-gateway-biooperation/api/fingerprint/inquiry/status/v2
        - services-gateway-demographic/api/person/deportee/info/v1
        - services-gateway-demographic/api/criminal/info/v2
        - services-gateway-demographic/api/criminal/identity/info/v1
        - services-gateway-demographic/api/criminal/info/basic/custom/v1
        - services-gateway-biooperation/api/criminal/report/v1
    
    menu.query.convictedReportInquiryBySearchCriteria:
        - services-gateway-lookups/api/application/person-type/v1
        - services-gateway-lookups/api/application/id-types/v1
        - services-gateway-lookups/api/application/nationality/all/v1
        - services-gateway-lookups/api/application/crime-types/v1
        - services-gateway-demographic/api/criminal/info/basic/custom/v1
        - services-gateway-biooperation/api/criminal/report/v1
    
    menu.cancel.deleteCompleteCriminalRecord:
        - services-gateway-lookups/api/application/person-type/v1
        - services-gateway-lookups/api/application/id-types/v1
        - services-gateway-lookups/api/application/nationality/all/v1
        - services-gateway-lookups/api/application/crime-types/v1
        - services-gateway-demographic/api/criminal/info/basic/custom/v1
        - services-gateway-biooperation/api/criminal/report/v1
        - services-gateway-biooperation/api/criminal/fingers/deletion/v1
        - services-gateway-biooperation/api/criminal/fingers/deletion/status/v1
        - services-gateway-biooperation/api/criminal/reports/deletion/v1
        
    menu.cancel.deleteCriminalFingerprints:
        - services-gateway-biooperation/api/criminal/fingers/deletion/v1
        - services-gateway-biooperation/api/criminal/fingers/deletion/status/v1
    
    menu.cancel.deleteConvictedReport:
        - services-gateway-lookups/api/application/person-type/v1
        - services-gateway-lookups/api/application/id-types/v1
        - services-gateway-lookups/api/application/nationality/all/v1
        - services-gateway-lookups/api/application/crime-types/v1
        - services-gateway-biooperation/api/criminal/report/v1
        - services-gateway-biooperation/api/criminal/report/deletion/v1
    
    menu.edit.editConvictedReport:
        - services-gateway-lookups/api/application/person-type/v1
        - services-gateway-lookups/api/application/id-types/v1
        - services-gateway-lookups/api/application/nationality/all/v1
        - services-gateway-lookups/api/application/crime-types/v1
        - services-gateway-biooperation/api/criminal/report/v1
        - services-gateway-demographic/api/criminal-system/update/full-report/v1
        - services-gateway-demographic/api/criminal-system/update/crime-Judgment/v1
    
    menu.query.faceVerification:
        - services-gateway-lookups/api/application/nationality/all/v1
        - services-gateway-biooperation/api/face/verify/v1
    
    menu.query.printDeadPersonRecord:
        - services-gateway-lookups/api/application/person-type/v1
        - services-gateway-lookups/api/application/id-types/v1
        - services-gateway-lookups/api/application/nationality/all/v1
        - services-gateway-biooperation/api/enrollment/dead-person/v1
        - services-gateway-demographic/api/person/info/v1
    
    menu.register.registerConvictedNotPresent:
        - services-gateway-lookups/api/application/person-type/v1
        - services-gateway-lookups/api/application/id-types/v1
        - services-gateway-lookups/api/application/nationality/all/v1
        - services-gateway-lookups/api/application/crime-types/v1
        - services-gateway-lookups/api/bio-exchange/crimes/v1
        - services-gateway-lookups/api/bio-exchange/parties/v1
        - services-gateway-demographic/api/person/info/v1
        - services-gateway-biooperation/api/fingerprint/images/v1
        - services-gateway-biooperation/api/fingerprint/available/v1
        - services-gateway-biooperation/api/fingerprint/images/v2
        - services-gateway-biooperation/api/fingerprint/available/v2
        - services-gateway-biooperation/api/criminal/fingerprint/images/v1
        - services-gateway-biooperation/api/xafis/nist/extraction/v1
        - services-gateway-biooperation/api/fingerprint/inquiry/v1
        - services-gateway-biooperation/api/fingerprint/inquiry/status/v2
        - services-gateway-demographic/api/person/deportee/info/v1
        - services-gateway-demographic/api/criminal/info/v2
        - services-gateway-demographic/api/criminal/identity/info/v1
        - services-gateway-biooperation/api/criminal/report/v1
        - services-gateway-biooperation/api/criminal/criminal-id/generation/v1
        - services-gateway-biooperation/api/criminal/fingers/registration/v1
        - services-gateway-biooperation/api/criminal/fingers/registration/status/v1
        - services-gateway-biooperation/api/xafis/report/v1
        - services-gateway-biooperation/api/gcc/criminal/exchange/v1
        - services-gateway-demographic/api/criminal/info/basic/custom/v1
        - services-gateway-biooperation/api/criminal/report/v1
    
    menu.register.registerConvictedPresent:
        - services-gateway-lookups/api/application/person-type/v1
        - services-gateway-lookups/api/application/id-types/v1
        - services-gateway-lookups/api/application/nationality/all/v1
        - services-gateway-lookups/api/application/crime-types/v1
        - services-gateway-lookups/api/bio-exchange/crimes/v1
        - services-gateway-lookups/api/bio-exchange/parties/v1
        - services-gateway-demographic/api/person/info/v1
        - services-gateway-biooperation/api/fingerprint/inquiry/v1
        - services-gateway-biooperation/api/fingerprint/inquiry/status/v2
        - services-gateway-demographic/api/person/deportee/info/v1
        - services-gateway-demographic/api/criminal/info/v2
        - services-gateway-demographic/api/criminal/identity/info/v1
        - services-gateway-biooperation/api/criminal/report/v1
        - services-gateway-biooperation/api/criminal/criminal-id/generation/v1
        - services-gateway-biooperation/api/criminal/fingers/registration/v1
        - services-gateway-biooperation/api/criminal/fingers/registration/status/v1
        - services-gateway-biooperation/api/xafis/report/v1
        - services-gateway-biooperation/api/gcc/criminal/exchange/v1
        - services-gateway-demographic/api/criminal/info/basic/custom/v1
        - services-gateway-biooperation/api/criminal/report/v1

    menu.register.registerCriminalFingerprintsNotPresent:
        - services-gateway-lookups/api/application/person-type/v1
        - services-gateway-lookups/api/application/id-types/v1
        - services-gateway-lookups/api/application/nationality/all/v1
        - services-gateway-demographic/api/person/info/v1
        - services-gateway-biooperation/api/fingerprint/images/v1
        - services-gateway-biooperation/api/fingerprint/available/v1
        - services-gateway-biooperation/api/fingerprint/images/v2
        - services-gateway-biooperation/api/fingerprint/available/v2
        - services-gateway-biooperation/api/criminal/fingerprint/images/v1
        - services-gateway-biooperation/api/xafis/nist/extraction/v1
        - services-gateway-biooperation/api/fingerprint/inquiry/v1
        - services-gateway-biooperation/api/fingerprint/inquiry/status/v2
        - services-gateway-biooperation/api/criminal/criminal-id/generation/v1
        - services-gateway-biooperation/api/criminal/fingers/registration/v1
        - services-gateway-biooperation/api/criminal/fingers/registration/status/v1
    
    menu.register.registerCriminalFingerprintsPresent:
        - services-gateway-biooperation/api/fingerprint/inquiry/v1
        - services-gateway-biooperation/api/fingerprint/inquiry/status/v2
        - services-gateway-biooperation/api/criminal/criminal-id/generation/v1
        - services-gateway-biooperation/api/criminal/fingers/registration/v1
        - services-gateway-biooperation/api/criminal/fingers/registration/status/v1
    
    menu.securityClearance.faceVerification:
        - services-gateway-lookups/api/application/nationality/all/v1
        - services-gateway-biooperation/api/face/verify/wanted/v1
    
    menu.query.searchByFaceImage:
        - services-gateway-biooperation/api/face/search/v1
    
    menu.register.visaApplicantsEnrollment:
        - services-gateway-lookups/api/application/nationality/all/v1
        - services-gateway-lookups/api/application/visa-type/all/v2
        - services-gateway-lookups/api/application/passport-types/v1
        - services-gateway-biooperation/api/enrollment/visa-applicant/v1