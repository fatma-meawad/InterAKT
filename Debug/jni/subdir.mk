################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../jni/CameraRenderer.cpp \
../jni/MarkerRegistration.cpp \
../jni/TargetDetector.cpp 

OBJS += \
./jni/CameraRenderer.o \
./jni/MarkerRegistration.o \
./jni/TargetDetector.o 

CPP_DEPS += \
./jni/CameraRenderer.d \
./jni/MarkerRegistration.d \
./jni/TargetDetector.d 


# Each subdirectory must supply rules for building sources it contributes
jni/%.o: ../jni/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"/Users/MESAI/Documents/workspaceCandy/NativeCamera/../../sdk/native/jni/include" -I/Users/MESAI/Development/Android/android-ndk-r8b/platforms/android-9/arch-arm/usr/include -I/Users/MESAI/Development/Android/android-ndk-r8b/sources/cxx-stl/gnu-libstdc++/4.6/include -I/Users/MESAI/Development/Android/android-ndk-r8b/sources/cxx-stl/gnu-libstdc++/4.6/libs/armeabi-v7a/include -Wall -g2 -gstabs -O0 -fpack-struct -fshort-enums -funsigned-char -funsigned-bitfields -fno-exceptions -mmcu=atmega16 -DF_CPU=1000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


