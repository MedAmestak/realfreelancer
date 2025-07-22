import axios from 'axios';

const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080/api',
    withCredentials: true, // This is crucial for sending cookies
});

// A function to get the token from wherever it's stored (e.g., AuthContext or localStorage)
let getAuthToken: () => string | null = () => null;
export const setGetAuthTokenFunction = (getter: () => string | null) => {
    getAuthToken = getter;
};

// A function for the interceptor to set the new token
let setAuthToken: ((token: string) => void) | null = null;
export const setAuthTokenSetter = (setter: (token: string) => void) => {
    setAuthToken = setter;
};


axiosInstance.interceptors.request.use(
    config => {
        const token = getAuthToken();
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
    response => response,
    async error => {
        const originalRequest = error.config;
        // Check if the error is 401, not from a refresh request, and we haven't retried yet
        if (error.response.status === 401 && originalRequest.url !== '/auth/refresh' && !originalRequest._retry) {
            originalRequest._retry = true;
            try {
                const { data } = await axiosInstance.post('/auth/refresh');
                if (setAuthToken) {
                    setAuthToken(data.accessToken);
                }
                originalRequest.headers['Authorization'] = `Bearer ${data.accessToken}`;
                return axiosInstance(originalRequest);
            } catch (refreshError) {
                // Logout logic
                if (setAuthToken) {
                    setAuthToken(''); // Clear token
                }
                window.location.href = '/login';
                return Promise.reject(refreshError);
            }
        }
        return Promise.reject(error);
    }
);

export default axiosInstance;