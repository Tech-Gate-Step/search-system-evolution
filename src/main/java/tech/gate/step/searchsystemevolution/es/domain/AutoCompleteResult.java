package tech.gate.step.searchsystemevolution.es.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoCompleteResult {


    private List<Suggestion> suggestions;


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Suggestion {
        private String text;
        private String type;
        private Long productId;
        private  Float score;
    }
}
