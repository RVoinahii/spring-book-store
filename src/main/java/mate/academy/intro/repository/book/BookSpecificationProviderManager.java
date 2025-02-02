package mate.academy.intro.repository.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.exceptions.SpecificationNotFoundException;
import mate.academy.intro.model.Book;
import mate.academy.intro.repository.SpecificationProvider;
import mate.academy.intro.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new SpecificationNotFoundException(
                        "Can't find correct specification provided for key " + key));
    }
}
