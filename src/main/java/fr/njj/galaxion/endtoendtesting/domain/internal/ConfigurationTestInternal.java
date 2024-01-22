package fr.njj.galaxion.endtoendtesting.domain.internal;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ConfigurationTestInternal {

  @Setter private String title;

  @Builder.Default @Setter private List<String> variables = new ArrayList<>();

  @Builder.Default @Setter private List<String> tags = new ArrayList<>();

  @Builder.Default @Setter private boolean toDisable = false;
}
