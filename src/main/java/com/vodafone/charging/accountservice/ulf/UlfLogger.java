package com.vodafone.charging.accountservice.ulf;

import com.vodafone.application.logging.ULFEntry;
import com.vodafone.application.logging.ULFEntry.Builder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface UlfLogger {

	void log(@Nonnull ULFEntry ulfEntry);

	void logPayload(@Nonnull Builder builder, @Nullable String payload);

	boolean isEnabledLogWithPayload();

}
