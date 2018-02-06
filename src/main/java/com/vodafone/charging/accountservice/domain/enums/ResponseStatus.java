package com.vodafone.charging.accountservice.domain.enums;


/**
 * An enum to encapsulate the possible return values from the ERIF
 * <ul>
 * <li>DENIED=0</li>
 * <li>ACCEPTED=1</li>
 * <li>REJECTED=2</li>
 * <li>ERROR=4</li>
 * </ul>
 * see the ERIF developer guide for details.
 */
public enum ResponseStatus {
    /**
     * The error is non-recoverable, and ER may close all subscriptions relating to this user.
     */
    DENIED(0),
    /**
     * everything went ok
     */
    ACCEPTED(1),
    /**
     * ER may retry the operation in the future and may get a different result
     */
    REJECTED(2),
    /**
     * DO NOT USE.  Added for legacy code only.
     *
     * @deprecated Use {@link ResponseType.USER_INVALID} instead
     */
    INVALID_BAN(3),
    /**
     * something needs attention on either opco or global side, or the request could not be understood
     */
    ERROR(4);


    private final int code;

    //values taken from PaymentAuthResponse

    ResponseStatus(int code) {
        this.code = code;
    }

    /**
     * the id of the response:<br/>
     * <ul>
     * <li>DENIED=0</li>
     * <li>ACCEPTED=1</li>
     * <li>REJECTED=2</li>
     * <li>ERROR=4</li>
     * </ul>
     *
     * @return
     */
    public int getId() {
        return code;
    }

    /**
     * gets a ResponseStatus which matches the supplied name (code) and id.
     *
     * @param name e.g. ACCEPTED
     * @param id   e.g. 1
     * @return {@link ResponseStatus}
     * @throws IllegalArgumentException if the name and id don't correspond to a valid ResponseStatus
     */
    public static ResponseStatus get(String name, int id) {
        if (isAccepted(name) && id == 1)
            return ACCEPTED;
        if (isDenied(name) && id == 0)
            return DENIED;
        if (isRejected(name) && id == 2)
            return REJECTED;
        if (isError(name) && id == 4)
            return ERROR;
        throw new IllegalArgumentException(name + " and id " + id + " are not a valid response status");
    }

    /**
     * ACCEPTED, REJECTED, DENIED or ERROR <br/>
     * same as toString()
     */
    public String getName() {
        return toString();
    }

    public boolean isAccepted() {
        return this.equals(ACCEPTED);
    }

    public boolean isRejected() {
        return this.equals(REJECTED);
    }

    public boolean isDenied() {
        return this.equals(DENIED);
    }

    public boolean isError() {
        return this.equals(ERROR);
    }

    /**
     * returns true if the String is "ACCEPTED", false otherwise.  Handles nulls without throwing exceptions.
     *
     * @param status
     * @return
     */
    public static boolean isAccepted(String status) {
        try {
            return ResponseStatus.valueOf(status).equals(ACCEPTED);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isRejected(String status) {
        try {
            return ResponseStatus.valueOf(status).equals(REJECTED);
        } catch (Exception e) {
            return false;
        }
    }


    public static boolean isDenied(String status) {
        try {
            return ResponseStatus.valueOf(status).equals(DENIED);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isError(String status) {
        try {
            return ResponseStatus.valueOf(status).equals(ERROR);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * is it denied, rejected or error - ie anything except ACCEPTED
     *
     * @return
     */
    public boolean isFailed() {
        return !this.equals(ACCEPTED);
    }

    /**
     * is it denied, rejected or error - ie anything except ACCEPTED
     *
     * @return
     */
    public static boolean isFailed(String status) {
        return (isRejected(status) || isDenied(status) || isError(status));
    }

    /**
     * Checks if the status supplied is valid (ACCEPTED, REJECTED, DENIED or ERROR)<br/>
     *
     * @param status
     * @return
     */
    public static boolean isValid(final String status) {
        try {
            //invalid ban is not valid, all others are.
            //the valueOf throws an exception if the string is not one of the ResponseStatus.values()
            return !ResponseStatus.valueOf(status).equals(INVALID_BAN);
        } catch (Exception e) {
            return false;
        }
    }


}
