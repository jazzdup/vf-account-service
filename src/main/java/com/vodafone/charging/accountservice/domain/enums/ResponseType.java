package com.vodafone.charging.accountservice.domain.enums;

import com.vodafone.charging.accountservice.domain.ERIFResponse;


/**
 * this enum encapsulates error id values (OK, CONTENT_BLOCKED, INSUFFICIENT FUNDS, etc),
 * with the corresponding {@link com.vizzavi.ecommerce.business.common.ResponseStatus} (ACCEPTED, DENIED, REJECTED, ERROR),
 * taking into account the restrictions documented in the ERIF developer guide.<p/>
 * Values include OK, USER_BARRED, CONTENT_BLOCKED, INSUFFICIENT_FUNDS, etc
 *
 * @author matt
 * @see ResponseStatus
 * @version $Id: ResponseType.java,v 1.6 2016-12-14 15:08:11 matt.darwin Exp $Id
 */
public enum ResponseType
{
    	/**ACCEPTED.  Everything ok*/
		OK(ResponseStatus.ACCEPTED),
		/**REJECTED. The user is not allowed access to the requested content*/
	    CONTENT_BLOCKED(ResponseStatus.REJECTED),
	    /**REJECTED. The user has reached a spend limit*/
	    USER_SPEND_LIMIT(ResponseStatus.REJECTED),
	    /**REJECTED. Another spend limit has been reached; eg the content partner, or a transaction limit*/
	    SPEND_LIMIT(ResponseStatus.REJECTED),
	    /**REJECTED. The user hasn't got enough funds in their account for the purchase*/
	    INSUFFICIENT_FUNDS(ResponseStatus.REJECTED),
	    /**REJECTED. The account has been temporarily suspended (e.g. for non-payment of bill)*/
	    USER_SUSPENDED(ResponseStatus.REJECTED),
	    /**REJECTED. Eg wrong currency, amount 0 or negative, capture amount greater than authorisation amount, transaction already refunded*/
	    AMOUNT_INVALID(ResponseStatus.REJECTED),
	    /**REJECTED. Use this when the scenario is not adequately described by one of the other REJECTED cases. Use the reason field to make it clear what happened.*/
	    REJECTED_OTHER(ResponseStatus.REJECTED),
	    /**DENIED. The user has left Vodafone or closed their account*/
	    USER_NOT_FOUND(ResponseStatus.DENIED),
	    /**DENIED. The msisdn is an invalid / unrecognised Vodafone msisdn. The msisdn has been recycled, or you believe the request was for a different user.*/
	    USER_INVALID(ResponseStatus.DENIED),
	    /**DENIED. The account has been permanently locked.*/
	    USER_BARRED(ResponseStatus.DENIED),
		/**DENIED. The locale in the request doesn't match user's profile locale.*/
		USER_INVALID_LOCALE(ResponseStatus.DENIED),
	    /**DENIED. Use this when the scenario is not adequately described by one of the other DENIED error Ids. Use the reason field to make it clear what happened*/
	    DENIED_OTHER(ResponseStatus.DENIED),
	    /**ERROR. The xml in the request was invalid or not well-formed*/
	    VALIDATION_ERROR(ResponseStatus.ERROR),
	    /**ERROR. An error occurred within the ERIF or one of the back-end systems behind it*/
	    SYSTEM_ERROR(ResponseStatus.ERROR),
		/**ERROR. Thrown when the account_type value is different from any of the following: vodafone_id*/
		USER_INVALID_ACCOUNT_TYPE(ResponseStatus.ERROR),
		/**REJECTED. Thrown when payment id is invalid*/
		INVALID_PAYMENT_ID(ResponseStatus.REJECTED),
		/**REJECTED. Thrown when original pspReference required for this operation*/
		INVALID_PSPREFERENCE(ResponseStatus.REJECTED),
		/**DENIED. Thrown when the credit card is not valid*/
		CARD_ERROR(ResponseStatus.DENIED),
		/**DENIED. Thrown when the credit card is possible fraud*/
		CARD_FRAUD(ResponseStatus.DENIED),
		/**REJECTED. Honestly no idea - from userinfo (vodafoneid)api. please update this comment*/
		USER_BLOCKED(ResponseStatus.REJECTED),
		/**
		 * JIRA ET-2057:KYC success would be verification_not_required|verified, KYC failed would be not-verified|in-progress|rejected;
		 */
		KYC_NOT_VERIFIED(ResponseStatus.REJECTED),
		KYC_IN_PROGRESS(ResponseStatus.REJECTED),
		KYC_REJECTED(ResponseStatus.REJECTED)
		;



	private final ResponseStatus status;


    private ResponseType(ResponseStatus status)	{
        this.status = status;
    }

    /**
     *ACCEPTED, REJECTED, DENIED or ERROR
     *
     * @return a {@link com.vizzavi.ecommerce.business.common.ResponseStatus} object.
     */
    public ResponseStatus getStatus()	{
    	return status;
    }
    
    

	/**
	 *1 for accepted, 2 for rejected, 3 for denied, 4 for error
	 *
	 * @return a int.
	 */
	public int getID() {
		return status.getId();
	}

	/**
	 *same as toString() - returns something like CONTENT_BLOCKED, USER_INVALID, etc
	 *
	 * @return a {@link String} object.
	 */
	public String getName() {
		return toString();
	}

	/**
	 * Is the supplied combination of error id and status valid?<br/>
	 *
	 * Pass in 2 strings for the error status and errorId, and this method will
	 * validates them based on the rules defined in ER-IF Developer guide.
	 *
	 * @param status e.g. "REJECTED"
	 * @param errorId e.g. "USER_NOT_FOUND"
	 * @return true if they are a valid combination, false otherwise
	 */
	public static boolean isValidCombination(String status, String errorId)	{
		try	{
			ResponseStatus statusEnum =ResponseStatus.valueOf(status);
			ResponseType responseType = valueOf(errorId);
			return responseType.getStatus().equals(statusEnum);
		}	catch(Exception e)	{	//valueOf() throws IllegalArgumentException
			//logger.info(e.getMessage());
			return false;
		}
	}

	/**
	 * Is the response's combination of error id and status valid?<br/>
	 *
	 * Pass in a Response, and this method will
	 * validates it based on the rules defined in ER-IF Developer guide.
	 *
	 * @param response a {@link com.vizzavi.ecommerce.message.beans.Response} object.
	 * @return a boolean.
	 */
	public static boolean isValidResponse(ERIFResponse response) {
		return isValidCombination(response.getStatus(), response.getErrId());
	}

	/**
	 * <p>get.</p>
	 *
	 * @param type a {@link String} object.
	 * @return a {@link String} object.
	 */
	public static String get(String type) {
		String response_type = "";
		try {
			response_type = ResponseType.valueOf(type).getStatus().toString();
		} catch (Exception ex) {
			
		}
		return response_type;
	}
}
