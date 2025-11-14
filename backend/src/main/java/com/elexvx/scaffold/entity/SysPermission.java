package com.elexvx.scaffold.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("sys_permission")
public class SysPermission {
    @TableId
    private Long id;
    private String permCode;
    private String permName;
    private Integer permType;
    private String resource;
    private String action;
    private String httpMethod;
    private String httpPath;
    private Integer effect;
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPermCode() { return permCode; }
    public void setPermCode(String permCode) { this.permCode = permCode; }
    public String getPermName() { return permName; }
    public void setPermName(String permName) { this.permName = permName; }
    public Integer getPermType() { return permType; }
    public void setPermType(Integer permType) { this.permType = permType; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    public String getHttpPath() { return httpPath; }
    public void setHttpPath(String httpPath) { this.httpPath = httpPath; }
    public Integer getEffect() { return effect; }
    public void setEffect(Integer effect) { this.effect = effect; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

