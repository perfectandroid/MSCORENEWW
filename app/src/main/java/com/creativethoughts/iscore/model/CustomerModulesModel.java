package com.creativethoughts.iscore.model;

public class CustomerModulesModel {

    private String SubModule;
    private String ModuleName;

    public CustomerModulesModel(String SubModule, String ModuleName) {
        this.SubModule = SubModule;
        this.ModuleName = ModuleName;
    }

    public String getSubModule() {
        return SubModule;
    }

    public void setSubModule(String subModule) {
        SubModule = subModule;
    }

    public String getModuleName() {
        return ModuleName;
    }

    public void setModuleName(String moduleName) {
        ModuleName = moduleName;
    }
}
