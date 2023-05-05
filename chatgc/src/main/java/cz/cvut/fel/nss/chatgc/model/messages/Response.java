package cz.cvut.fel.nss.chatgc.model.messages;

import cz.cvut.fel.nss.chatgc.model.users.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "response")
@DiscriminatorValue(value="RESPONSE")
public class Response extends Message{

    @ManyToOne(optional = false)
    private Employee employee;
}
