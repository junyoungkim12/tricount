package extricount.tricountapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Settlement {
    private Long id;
    private String name;
    private List<Member> participants = new ArrayList<>();
}
