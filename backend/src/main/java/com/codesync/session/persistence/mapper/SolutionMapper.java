package com.codesync.session.persistence.mapper;

import com.codesync.session.domain.enumtype.ProgrammingLanguage;
import com.codesync.session.domain.valueobject.CodeFingerprint;
import com.codesync.session.domain.valueobject.Solution;
import com.codesync.session.domain.valueobject.SourceCode;
import com.codesync.session.persistence.entity.SolutionEntity;
import org.springframework.stereotype.Component;

@Component
public class SolutionMapper {

    public SolutionEntity toEntity(Solution domain) {

        SolutionEntity entity = SolutionEntity.create();

        entity.setCode(domain.sourceCode().value());

        entity.setLanguage(
                domain.language().name()
        );

        entity.setFingerprint(
                domain.fingerprint().value()
        );

        return entity;
    }

    public Solution toDomain(SolutionEntity entity) {

        return new Solution(

                new SourceCode(
                        entity.getCode()
                ),

                ProgrammingLanguage.valueOf(
                        entity.getLanguage()
                ),

                new CodeFingerprint(
                        entity.getFingerprint()
                )
        );
    }
}