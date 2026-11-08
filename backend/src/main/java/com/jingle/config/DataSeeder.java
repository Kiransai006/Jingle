package com.jingle.config;

import com.jingle.model.MenuItem;
import com.jingle.model.Restaurant;
import com.jingle.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds sample Andhra restaurants and dishes on first start (only when the DB is empty).
 * Images are served from Wikimedia Commons - see README "Image credits".
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final String IMG = "https://en.wikipedia.org/wiki/Special:FilePath/";

    // Image file names (Wikimedia Commons)
    private static final String ANDHRA_MEALS = "Vegetarian_Andhra_Meal.jpg";
    private static final String CHICKEN_BIRYANI = "Hyderabadi_Chicken_Biryani.jpg";
    private static final String MUTTON_BIRYANI = "Hyderabadi_mutton_biryani.jpg";
    private static final String VEG_BIRYANI = "Hyderabadi_Vegetable_Biryani.jpg";
    private static final String BIRYANI_COMBO = "Hyderabadi_Biryani_with_Raita,_Mirchi_Ka_Salan_and_Salad.JPG";
    private static final String SPICY_BIRYANI = "Hyderabadi_Chicken_biriyani.jpg";
    private static final String PESARATTU = "Pesarattu.jpg";
    private static final String MLA_PESARATTU = "MLA_Pesarattu.JPG";
    private static final String IDLI_VADA = "Idly_sambar_vada.JPG";
    private static final String IDLI = "Idli_sambar.JPG";
    private static final String PUNUGULU = "Punugulu_with_chutney.JPG";
    private static final String MIRCHI_BAJJI = "Mirapakaya_Bajji_or_Mirchi_Bhajji.JPG";
    private static final String PULIHORA = "PULIHORA.JPG";
    private static final String GUTTI_VANKAYA = "Gutti_vankaya_curry.jpg";
    private static final String GUTTI_VANKAYA_KURA = "Gutti_Vankaya_Kura.jpg";
    private static final String GONGURA_PACHADI = "A_famous_andhra_side_dish._Gongura_paccaDi.JPG";
    private static final String GONGURA_PICKLE = "Gongura_Pickle.jpg";
    private static final String CHICKEN_65 = "Chicken_65_(Dish).jpg";
    private static final String DOUBLE_KA_MEETHA = "Double_Ka_Meetha.JPG";
    private static final String POOTHAREKULU = "Pootharekulu_dry_fruits.jpg";
    private static final String POOTHAREKULU_CLASSIC =
            "Pootharekulu_or_Poothareku_(singular)_is_a_popular_sweet_from_Atreyapuram,_East_Godavari_District_of_Andhrapradesh-1.JPG";

    private final RestaurantRepository restaurantRepository;

    public DataSeeder(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    static String img(String file) {
        return IMG + file + "?width=640";
    }

    @Override
    public void run(String... args) {
        if (restaurantRepository.count() > 0) {
            log.info("Data already present - skipping seed");
            return;
        }

        Restaurant godavari = new Restaurant("Godavari Ruchulu", "Andhra Meals, Konaseema",
                "Ameerpet, Hyderabad", 4.5, 30, 400, img(ANDHRA_MEALS), "20% OFF up to ₹100")
                .addMenuItem(new MenuItem("Andhra Veg Meals", "Rice, pappu, sambar, rasam, two curries, pickle, curd and papad", 199, true, true, img(ANDHRA_MEALS)))
                .addMenuItem(new MenuItem("Pulihora", "Tangy tamarind rice tempered with peanuts and curry leaves", 129, true, true, img(PULIHORA)))
                .addMenuItem(new MenuItem("Gutti Vankaya Kura", "Stuffed baby brinjal in a spicy peanut-sesame masala", 179, true, false, img(GUTTI_VANKAYA)))
                .addMenuItem(new MenuItem("Gongura Pachadi", "Sour sorrel-leaf chutney - the pride of Andhra", 89, true, false, img(GONGURA_PACHADI)))
                .addMenuItem(new MenuItem("Double Ka Meetha", "Bread pudding soaked in saffron milk with dry fruits", 99, true, false, img(DOUBLE_KA_MEETHA)));

        Restaurant nizam = new Restaurant("Nizam's Dum Biryani House", "Biryani, Hyderabadi",
                "Banjara Hills, Hyderabad", 4.6, 35, 500, img(CHICKEN_BIRYANI), "Free delivery")
                .addMenuItem(new MenuItem("Hyderabadi Chicken Dum Biryani", "Slow-cooked basmati and marinated chicken, served with raita", 299, false, true, img(CHICKEN_BIRYANI)))
                .addMenuItem(new MenuItem("Mutton Dum Biryani", "Tender mutton layered with saffron rice", 379, false, true, img(MUTTON_BIRYANI)))
                .addMenuItem(new MenuItem("Veg Dum Biryani", "Seasonal vegetables and fragrant basmati, dum style", 229, true, false, img(VEG_BIRYANI)))
                .addMenuItem(new MenuItem("Biryani Combo", "Chicken biryani with mirchi ka salan, raita and salad", 349, false, false, img(BIRYANI_COMBO)))
                .addMenuItem(new MenuItem("Double Ka Meetha", "Classic Hyderabadi dessert", 99, true, false, img(DOUBLE_KA_MEETHA)));

        Restaurant tiffins = new Restaurant("Vijayawada Tiffin Junction", "South Indian, Tiffins",
                "Benz Circle, Vijayawada", 4.4, 20, 200, img(PESARATTU), "₹50 OFF above ₹199")
                .addMenuItem(new MenuItem("Pesarattu", "Crispy green-gram dosa with ginger chutney", 89, true, true, img(PESARATTU)))
                .addMenuItem(new MenuItem("MLA Pesarattu", "Pesarattu stuffed with upma - a Vijayawada favourite", 119, true, true, img(MLA_PESARATTU)))
                .addMenuItem(new MenuItem("Idli Vada Sambar", "Two idlis, one vada, sambar and coconut chutney", 79, true, false, img(IDLI_VADA)))
                .addMenuItem(new MenuItem("Punugulu", "Crispy fried batter bites with peanut chutney", 69, true, false, img(PUNUGULU)))
                .addMenuItem(new MenuItem("Mirchi Bajji", "Stuffed green chilli fritters", 59, true, false, img(MIRCHI_BAJJI)));

        Restaurant guntur = new Restaurant("Guntur Mirchi Kitchen", "Spicy Andhra, Chicken",
                "Brodipet, Guntur", 4.3, 40, 450, img(CHICKEN_65), "Flat ₹75 OFF")
                .addMenuItem(new MenuItem("Chicken 65", "Fiery deep-fried chicken with curry leaves and green chilli", 249, false, true, img(CHICKEN_65)))
                .addMenuItem(new MenuItem("Guntur Spicy Chicken Biryani", "Extra-hot biryani with Guntur chillies", 279, false, true, img(SPICY_BIRYANI)))
                .addMenuItem(new MenuItem("Gongura Pickle", "Home-style sorrel-leaf pickle (250 g)", 149, true, false, img(GONGURA_PICKLE)))
                .addMenuItem(new MenuItem("Mirchi Bajji", "Guntur chilli fritters with onion topping", 59, true, false, img(MIRCHI_BAJJI)));

        Restaurant sweets = new Restaurant("Atreyapuram Sweets & Snacks", "Sweets, Snacks",
                "Danavaipeta, Rajahmundry", 4.7, 25, 250, img(POOTHAREKULU), "Buy 1 Get 1")
                .addMenuItem(new MenuItem("Pootharekulu (Classic)", "Paper-thin rice sheets with ghee and sugar - box of 5", 199, true, true, img(POOTHAREKULU_CLASSIC)))
                .addMenuItem(new MenuItem("Dry Fruit Pootharekulu", "Loaded with cashew, almond and jaggery - box of 5", 249, true, true, img(POOTHAREKULU)))
                .addMenuItem(new MenuItem("Double Ka Meetha", "Rich bread pudding", 99, true, false, img(DOUBLE_KA_MEETHA)))
                .addMenuItem(new MenuItem("Punugulu", "Evening snack with chutney", 69, true, false, img(PUNUGULU)));

        Restaurant rayalaseema = new Restaurant("Rayalaseema Ruchulu", "Andhra, Home Style",
                "Kurnool", 4.2, 30, 350, img(GUTTI_VANKAYA_KURA), "")
                .addMenuItem(new MenuItem("Gutti Vankaya Kura", "Signature stuffed brinjal curry", 169, true, true, img(GUTTI_VANKAYA_KURA)))
                .addMenuItem(new MenuItem("Andhra Veg Meals", "Unlimited home-style meals", 189, true, true, img(ANDHRA_MEALS)))
                .addMenuItem(new MenuItem("Pulihora", "Temple-style tamarind rice", 119, true, false, img(PULIHORA)))
                .addMenuItem(new MenuItem("Gongura Pachadi", "Fresh sorrel-leaf chutney", 89, true, false, img(GONGURA_PACHADI)))
                .addMenuItem(new MenuItem("Idli Sambar", "Soft idlis with sambar", 59, true, false, img(IDLI)));

        restaurantRepository.saveAll(List.of(godavari, nizam, tiffins, guntur, sweets, rayalaseema));
        log.info("Seeded {} restaurants", restaurantRepository.count());
    }
}
