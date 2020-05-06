package io.specialrooter.plus.mybatisplus.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.specialrooter.plus.mybatisplus.x.LongJsonDeserializer;
import io.specialrooter.plus.mybatisplus.x.LongJsonSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper= false)
public class BaseModel extends SupperClass {
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.INPUT)
    @JsonDeserialize(using= LongJsonDeserializer.class)
    @JsonSerialize(using= LongJsonSerializer.class)
    @JsonView(BasicView.class)
    protected Long id;

    public interface BasicView{};

}
