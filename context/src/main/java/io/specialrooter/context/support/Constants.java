package io.specialrooter.context.support;

import io.specialrooter.context.model.DictItemModel;

import java.util.Dictionary;
import java.util.Hashtable;

public class Constants {
    public static final String DEFAULT_DICT = "default_dict";
    public static Dictionary<String, Dictionary<String, DictItemModel>> DICT = new Hashtable<>();
}
