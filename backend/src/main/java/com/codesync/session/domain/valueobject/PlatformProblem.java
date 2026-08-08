package com.codesync.session.domain.valueobject;

import com.codesync.session.domain.enumtype.Platform;

import java.util.Set;

/*
 * Immutable snapshot of a problem as provided by the coding platform.
 | Field                 | Why                              |
| --------------------- | -------------------------------- |
| `platformProblemId`   | Stable platform identifier       |
| `frontendProblemId`   | Display ID (LeetCode 1, 2, 3...) |
| `canonicalProblemKey` | Cross-platform mapping           |
| `title`               | Display                          |
| `slug`                | URL + GitHub folder              |
| `url`                 | Direct navigation                |
| `platform`            | LeetCode/GFG/etc.                |
| `officialDifficulty`  | Original platform difficulty     |
| `officialTags`        | Platform tags                    |
| `premium`             | Future filtering                 |
| `problemVersion`      | Future-proofing                  |
 */
public record PlatformProblem(

        String platformProblemId,

        String frontendProblemId,

        String canonicalProblemKey,

        String title,

        String slug,

        String url,

        Platform platform,

        String officialDifficulty,

        Set<String> officialTags,

        boolean premium,

        String problemVersion

) {

    public PlatformProblem {

        if (platformProblemId == null || platformProblemId.isBlank()) {
            throw new IllegalArgumentException("Platform problem ID cannot be null or blank.");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Problem title cannot be null or blank.");
        }

        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException("Problem slug cannot be null or blank.");
        }

        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Problem URL cannot be null or blank.");
        }

        if (platform == null) {
            throw new IllegalArgumentException("Platform cannot be null.");
        }

        if (officialDifficulty == null || officialDifficulty.isBlank()) {
            throw new IllegalArgumentException("Official difficulty cannot be null or blank.");
        }

        officialTags = officialTags == null ? Set.of() : Set.copyOf(officialTags);
    }
}