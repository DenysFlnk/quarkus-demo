package com.web_socket.dto;

import com.quarkus.model.Hobby;
import com.quarkus.model.Location;
import com.quarkus.model.OperationalStatus;
import java.util.List;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
public class ShoppingMallUpdateRequestDto {

    private Integer id;

    private String name;

    private OperationalStatus status;

    private JsonNullable<Location> location = JsonNullable.undefined();

    private JsonNullable<List<Hobby>> hobbies = JsonNullable.undefined();
}
