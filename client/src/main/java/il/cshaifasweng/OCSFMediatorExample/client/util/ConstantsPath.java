package il.cshaifasweng.OCSFMediatorExample.client.util;

import javafx.scene.effect.BoxBlur;
import java.util.Objects;

public class ConstantsPath {

    public static final String TITLE = "BookMySeat";
    public static final Double MIN_WIDTH = 1040.00;
    public static final Double MIN_HEIGHT = 640.00;

    public static final String SOURCE_PACKAGE = "/il/cshaifasweng/OCSFMediatorExample/client/";
    public static final String BOUNDARIES_PACKAGE = SOURCE_PACKAGE + "boundaries/";
    public static final String REPORTS_PACKAGE = BOUNDARIES_PACKAGE + "reports/";
    public static final String CONTENT_MANAGER_PACKAGE = BOUNDARIES_PACKAGE + "contentManager/";
    public static final String CUSTOMER_SERVICE_PACKAGE = BOUNDARIES_PACKAGE + "customerService/";
    public static final String REGISTER_USER_PACKAGE = BOUNDARIES_PACKAGE + "registeredUser/";
    public static final String USER_PACKAGE = BOUNDARIES_PACKAGE + "user/";
    public static final String COMPANY_MANAGER_PACKAGE = BOUNDARIES_PACKAGE + "companyManager/";
    public static final String THEATER_MANAGER_PACKAGE = BOUNDARIES_PACKAGE + "theaterManager/";

    /* USER_VIEWS */
    public static final String START_VIEW = USER_PACKAGE + "StartView.fxml";
    public static final String ABOUT_VIEW = USER_PACKAGE + "AboutView.fxml";
    public static final String HOME_VIEW = USER_PACKAGE + "HomeView.fxml";
    public static final String ME_PURCHASE_VIEW = USER_PACKAGE + "MEPurchaseView.fxml";
    public static final String HOME_VIEWING_PURCHASE_VIEW = USER_PACKAGE + "HomeViewingPurchase.fxml";
    public static final String MOVIE_INFO_VIEW = USER_PACKAGE + "MovieInfo.fxml";
    public static final String MOVIE_SMALL_VIEW = USER_PACKAGE + "MovieSmall.fxml";
    public static final String THEATER_PURCHASE_VIEW = USER_PACKAGE + "TheaterPurchaseView.fxml";
    public static final String COMPLAINT_VIEW = USER_PACKAGE + "ComplaintView.fxml";

    /* COMPANY_MANAGER_VIEWS */
    public static final String PRICE_CHANGE_VIEW = COMPANY_MANAGER_PACKAGE + "PriceChangeView.fxml";

    /* REGISTER_USER_VIEWS */
    public static final String DIALOG_COMPLAINT_VIEW = REGISTER_USER_PACKAGE + "dialogComplaint.fxml";
    public static final String ORDERS_VIEW = REGISTER_USER_PACKAGE + "OrdersView.fxml";
    public static final String DIALOG_TICKET_VIEW = REGISTER_USER_PACKAGE + "dialogTicket.fxml";

    /* CONTENT_MANAGER_VIEWS */
    public static final String CONTENT_MOVIES_VIEW = CONTENT_MANAGER_PACKAGE + "EditMovieListView.fxml";
    public static final String CONTENT_SCREENINGS_VIEW = CONTENT_MANAGER_PACKAGE + "EditMovieScreeningsView.fxml";
    public static final String DIALOG_MOVIE_VIEW = CONTENT_MANAGER_PACKAGE + "dialogEditMovie.fxml";
    public static final String DIALOG_SCREENING_VIEW = CONTENT_MANAGER_PACKAGE + "dialogEditScreening.fxml";

    /* CUSTOMER_MANAGER_VIEWS */
    public static final String CUSTOMER_SERVICE_VIEW = CUSTOMER_SERVICE_PACKAGE + "CustomerService.fxml";
    public static final String DIALOG_CUSTOMER_SERVICE_VIEW = CUSTOMER_SERVICE_PACKAGE + "dialogCustomerService.fxml";

    /* REPORTS_VIEWS */
    public static final String COMPANY_MANAGER_VIEW = REPORTS_PACKAGE + "ReportsView.fxml";
    public static final String THEATER_MANAGER_VIEW = THEATER_MANAGER_PACKAGE + "ManageTheaterView.fxml";

    public static final String ICON_PACKAGE = SOURCE_PACKAGE + "icons/";
    public static final String GENERAL_PACKAGE = ICON_PACKAGE + "general/";
    public static final String GENRE_PACKAGE = ICON_PACKAGE + "genre/";

    public static final String STAGE_ICON = GENERAL_PACKAGE + "icon.jpeg";
    public static final String NO_IMAGE_AVAILABLE = GENERAL_PACKAGE + "empty-image.jpg";
    public static final String INFORMATION_IMAGE = GENERAL_PACKAGE + "information.png";
    public static final String ERROR_IMAGE = GENERAL_PACKAGE + "error.png";
    public static final String SUCCESS_IMAGE = GENERAL_PACKAGE + "success.png";
    public static final String LOGIN_ICON = GENERAL_PACKAGE + "login.png";
    public static final String LOGOUT_ICON = GENERAL_PACKAGE + "logout.png";

    public static final String CSS_LIGHT_THEME = SOURCE_PACKAGE + "css/main.css";
    public static final String LIGHT_THEME = Objects.requireNonNull(ConstantsPath.class.getResource(CSS_LIGHT_THEME)).toExternalForm();

    public static final String MESSAGE_IMAGE_LARGE = "Please upload a picture smaller than 1 MB.";
    public static final String MESSAGE_NO_RECORD_SELECTED = "Select an item from the table.";

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String TIME_FORMAT = "HH:mm";

    public static final BoxBlur BOX_BLUR_EFFECT = new BoxBlur(3, 3, 3);
}
