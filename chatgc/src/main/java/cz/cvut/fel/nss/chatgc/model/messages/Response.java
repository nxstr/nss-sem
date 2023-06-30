package cz.cvut.fel.nss.chatgc.model.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Concrete Message class representing Response entity.
 */

@Data
@NoArgsConstructor
@Entity
@Table(name = "response")
@DiscriminatorValue(value = "RESPONSE")
public class Response extends Message {

    @ManyToOne(optional = false)
    private Employee employee;

    private Response(ResponseBuilder builder) {
        super(builder);
        this.employee = builder.employee;
    }

    public static class ResponseBuilder extends Message.MessageBuilder<ResponseBuilder> {
        private Employee employee;

        public ResponseBuilder() {
        }

        public ResponseBuilder addEmployee(Employee employee) {
            this.employee = employee;
            return this;
        }

        @Override
        public Response build() {
            return new Response(this);
        }

        @Override
        protected ResponseBuilder self() {
            return this;
        }
    }
}
