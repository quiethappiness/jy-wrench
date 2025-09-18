package io.github.quiethappiness.lua.manager.domain.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LuaScriptVO
{
	private String name;
	private String cache;
	private String sha;
	private Long lastModified=0L;
	private String version;
	private String path;
}
