/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.safetycenter;

import static android.os.Build.VERSION_CODES.TIRAMISU;

import static java.util.Objects.requireNonNull;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data class used by safety sources to propagate safety information such as their safety status and
 * safety issues.
 *
 * @hide
 */
@SystemApi
// TODO(b/207399899): Add timestamp field(s) to data model classes.
@RequiresApi(TIRAMISU)
public final class SafetySourceData implements Parcelable {

    @NonNull
    public static final Parcelable.Creator<SafetySourceData> CREATOR =
            new Parcelable.Creator<SafetySourceData>() {
                @Override
                public SafetySourceData createFromParcel(Parcel in) {
                    String id = requireNonNull(in.readString());
                    SafetySourceStatus status =
                            in.readParcelable(SafetySourceStatus.class.getClassLoader(),
                                    SafetySourceStatus.class);
                    List<SafetySourceIssue> issues = new ArrayList<>();
                    in.readParcelableList(issues, SafetySourceIssue.class.getClassLoader());
                    return new SafetySourceData(id, status, issues);
                }

                @Override
                public SafetySourceData[] newArray(int size) {
                    return new SafetySourceData[size];
                }
            };

    @NonNull
    private final String mId;
    @Nullable
    private final SafetySourceStatus mStatus;
    @NonNull
    private final List<SafetySourceIssue> mIssues;

    private SafetySourceData(@NonNull String id, @Nullable SafetySourceStatus status,
            @NonNull List<SafetySourceIssue> issues) {
        this.mId = id;
        this.mStatus = status;
        this.mIssues = new ArrayList<>(issues);
    }

    /**
     * Returns the id of the associated safety source.
     *
     * <p>The id uniquely identifies a safety source within the scope of the application that is
     * creating the source.
     */
    @NonNull
    public String getId() {
        return mId;
    }

    /** Returns the data for the safety source status to be shown in UI. */
    @Nullable
    public SafetySourceStatus getStatus() {
        return mStatus;
    }

    /** Returns the data for the list of safety source issues to be shown in UI. */
    @NonNull
    public List<SafetySourceIssue> getIssues() {
        return new ArrayList<>(mIssues);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeParcelable(mStatus, flags);
        dest.writeParcelableList(mIssues, flags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SafetySourceData)) return false;
        SafetySourceData that = (SafetySourceData) o;
        return mId.equals(that.mId) && Objects.equals(mStatus, that.mStatus)
                && mIssues.equals(that.mIssues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mStatus, mIssues);
    }

    @Override
    public String toString() {
        return "SafetySourceData{"
                + "mId='"
                + mId
                + '\''
                + ", mStatus="
                + mStatus
                + ", mIssues="
                + mIssues
                + '}';
    }

    /** Builder class for {@link SafetySourceData}. */
    public static final class Builder {
        @NonNull
        private final String mId;
        @NonNull
        private final List<SafetySourceIssue> mIssues = new ArrayList<>();
        @Nullable
        private SafetySourceStatus mStatus;

        /**
         * Creates a {@link Builder} for a {@link SafetySourceData}.
         *
         * @param id uniquely identifies the associated safety source, scoped within the application
         *           that is creating the associated safety source.
         */
        public Builder(@NonNull String id) {
            this.mId = requireNonNull(id);
        }

        /** Sets data for the safety source status to be shown in UI. */
        @NonNull
        public Builder setStatus(@Nullable SafetySourceStatus status) {
            mStatus = status;
            return this;
        }

        /** Adds data for a safety source issue to be shown in UI. */
        @NonNull
        public Builder addIssue(@NonNull SafetySourceIssue safetySourceIssue) {
            mIssues.add(requireNonNull(safetySourceIssue));
            return this;
        }

        /**
         * Clears data for all the safety source issues that were added to this {@link Builder}.
         */
        @NonNull
        public Builder clearIssues() {
            mIssues.clear();
            return this;
        }

        /** Creates the {@link SafetySourceData} defined by this {@link Builder}. */
        @NonNull
        public SafetySourceData build() {
            // TODO(b/207329841): Validate data matches validation in S, for eg that the status
            //  and severity levels of the settings and issues are compatible.
            return new SafetySourceData(mId, mStatus, mIssues);
        }
    }
}