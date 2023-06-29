package cz.cvut.fel.nss.chatgc.model.messages;

import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Concrete Message class representing Request entity.
 */

@Data
@NoArgsConstructor
@Entity
@Table(name = "requests")
@DiscriminatorValue(value="REQUEST")
public class Request extends Message{

    @ManyToMany
    private Set<Category> categories;

    private Request(RequestBuilder builder){
        super(builder);
        categories = builder.categories;
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
    }



    public static class RequestBuilder extends Message.MessageBuilder<RequestBuilder>{
        private Set<Category> categories = new HashSet<>();

        public RequestBuilder(){
        }

        public RequestBuilder addCategories(Set<Category> categories){
            this.categories = categories;
            return this;
        }

        public RequestBuilder addCategory(Category category){
            this.categories.add(category);
            return this;
        }

        @Override
        public Request build(){
            return new Request(this);
        }

        @Override
        protected RequestBuilder self() {
            return this;
        }
    }
}
