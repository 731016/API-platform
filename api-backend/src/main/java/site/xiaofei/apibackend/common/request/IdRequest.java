package site.xiaofei.apibackend.common.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class IdRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

}