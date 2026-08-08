package com.codesync.session.persistence.mapper;

import com.codesync.session.domain.valueobject.PlatformProblem;
import com.codesync.session.persistence.entity.PlatformProblemEntity;
import com.codesync.session.persistence.entity.ProblemTagEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PlatformProblemMapper {

    public PlatformProblemEntity toEntity(PlatformProblem domain) {

        PlatformProblemEntity entity = PlatformProblemEntity.create();

        entity.setPlatformProblemId(domain.platformProblemId());
        entity.setFrontendProblemId(domain.frontendProblemId());
        entity.setCanonicalProblemKey(domain.canonicalProblemKey());
        entity.setTitle(domain.title());
        entity.setSlug(domain.slug());
        entity.setUrl(domain.url());
        entity.setPlatform(domain.platform().name());
        entity.setOfficialDifficulty(domain.officialDifficulty());
        entity.setPremium(domain.premium());
        entity.setProblemVersion(domain.problemVersion());

        if (domain.officialTags() != null) {
            domain.officialTags().forEach(tag -> {

                ProblemTagEntity tagEntity = ProblemTagEntity.create();

                tagEntity.setTag(tag);

                entity.addTag(tagEntity);
            });
        }

        return entity;
    }

    public PlatformProblem toDomain(PlatformProblemEntity entity) {

        Set<String> tags = entity.getTags()
                .stream()
                .map(ProblemTagEntity::getTag)
                .collect(Collectors.toSet());

        return new PlatformProblem(
                entity.getPlatformProblemId(),
                entity.getFrontendProblemId(),
                entity.getCanonicalProblemKey(),
                entity.getTitle(),
                entity.getSlug(),
                entity.getUrl(),
                com.codesync.session.domain.enumtype.Platform.valueOf(
                        entity.getPlatform()
                ),
                entity.getOfficialDifficulty(),
                tags,
                entity.isPremium(),
                entity.getProblemVersion()
        );
    }
}