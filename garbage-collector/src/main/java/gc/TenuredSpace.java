package gc;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class TenuredSpace {
    private List<Object> list;

    public TenuredSpace() {
        init();
    }

    private void init() {
        list = Collections.emptyList();
    }
}
