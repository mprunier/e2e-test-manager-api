package fr.njj.galaxion.endtoendtesting.domain.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ArtifactDataInternal {

    @Setter
    @Builder.Default
    private Map<String, byte[]> videos = new HashMap<>();

    @Setter
    @Builder.Default
    private Map<String, byte[]> screenshots = new HashMap<>();

    @Setter
    private MochaReportInternal report;
}
