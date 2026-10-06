package megalodonte;
import java.util.List;
import java.util.ArrayList;
import megalodonte.base.state.State;
import megalodonte.v2.ListState;

public final class PortableStateTest {
    static void require(boolean value) { if (!value) throw new AssertionError("Portable reactivity contract failed"); }
    public static void main(String[] args) {
        State<String> source = State.of("a");
        ComputedState<Integer> computed = ComputedState.of(() -> source.get().length(), source);
        List<Integer> values = new ArrayList<>();
        var subscription = computed.observe(values::add);
        source.set("ab"); subscription.close(); source.set("abc");
        require(values.equals(List.of(1,2)));
        computed.close(); source.set("abcd"); require(computed.get() == 3);
        ListState<String> items = ListState.ofEmpty();
        ForEachState<String,String> each = ForEachState.of(items, String::toUpperCase);
        items.add("hello"); require(each.getComponents().equals(List.of("HELLO")));
        each.close(); items.add("bye"); require(each.getComponents().equals(List.of("HELLO")));
        List<Integer> sizes = new ArrayList<>();
        items.subscribe(value -> sizes.add(value.size()));
        ListenerManager.disposeAll(); items.add("end");
        require(sizes.equals(List.of(2)) && ListenerManager.getListenerCount() == 0);
        System.out.println("Portable computed/list/reconciliation/disposal tests passed");
    }
}
