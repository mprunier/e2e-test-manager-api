package fr.njj.galaxion.endtoendtesting.usecases.pipeline;

import fr.njj.galaxion.endtoendtesting.service.AssertPipelineService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AssertPipelineUseCase {

    private final AssertPipelineService assertPipelineService;

    public void execute() {
        assertPipelineService.assertPipeline();
    }
}
