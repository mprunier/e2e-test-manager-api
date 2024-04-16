package fr.njj.galaxion.endtoendtesting.domain.request;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SearchConfigurationRequest {

    @Setter
    @QueryParam("configurationSuiteId")
    private Long configurationSuiteId;

    @Setter
    @QueryParam("configurationTestId")
    private Long configurationTestId;

    @QueryParam("configurationTestIdentifier")
    private String configurationTestIdentifier;

    @Setter
    private List<Long> newConfigurationSuiteIds;

    @Setter
    private List<Long> configurationSuiteIds;

    @Setter
    private List<Long> configurationIdentifierIds;

    @QueryParam("file")
    private String file;

    @QueryParam("status")
    private ConfigurationStatus status;

    @QueryParam("allNotSuccess")
    private Boolean allNotSuccess;

    @QueryParam("page")
    @DefaultValue("0")
    private int page;

    @QueryParam("size")
    @DefaultValue("10")
    private int size;
}
