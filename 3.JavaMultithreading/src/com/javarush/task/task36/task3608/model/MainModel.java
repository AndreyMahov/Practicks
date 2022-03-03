package com.javarush.task.task36.task3608.model;

import com.javarush.task.task36.task3608.bean.User;
import com.javarush.task.task36.task3608.model.service.UserService;
import com.javarush.task.task36.task3608.model.service.UserServiceImpl;

import java.util.List;

public class MainModel implements Model {

    //use helpful services
    private UserService userService = new UserServiceImpl();

    //use special object to keep data for view rendering
    private ModelData modelData = new ModelData();

    @Override
    public ModelData getModelData() {
        return modelData;
    }

    @Override
    public void loadUsers() {
        List<User> users = getAllUsers();
        //refresh model data
        modelData.setUsers(users);
        modelData.setDisplayDeletedUserList(false);
    }
    public void loadDeletedUsers() {
        List<User> users = userService.getAllDeletedUsers();
        //refresh model data
        modelData.setUsers(users);
        modelData.setDisplayDeletedUserList(true);
    }
    public void loadUserById(long userId) {
        User user = userService.getUsersById(userId);
modelData.setActiveUser(user);
    }

    @Override
    public void deleteUserById(long id){
        userService.deleteUser(id);
        modelData.setUsers(getAllUsers());
    }

    private   List<User> getAllUsers(){
List<User> temp = userService.getUsersBetweenLevels(1,100);
return userService.filterOnlyActiveUsers(temp);
    }
    @Override
    public void changeUserData(String name, long id, int level) {
        userService.createOrUpdateUser(name, id, level);
        List<User> users = getAllUsers();
        //refresh model data
        modelData.setUsers(users);
    }

    }
