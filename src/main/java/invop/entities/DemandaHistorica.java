package invop.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;


@Entity
@Table(name = "DemandaHistorica")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class DemandaHistorica extends Base {

    @NotNull
    @Column(name = "demanda_historica")
    private Date fecha_desde;

    @NotNull
    @Column(name = "fecha_hasta")
    private Date fecha_hasta;

}
