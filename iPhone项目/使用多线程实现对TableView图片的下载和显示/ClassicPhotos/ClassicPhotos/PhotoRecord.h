//
//  PhotoRecord.h
//  ClassicPhotos
//
//  Created by l l on 16/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface PhotoRecord : NSObject

@property (nonatomic,strong) NSString *name;
@property (nonatomic,strong) UIImage *image;
@property (nonatomic,strong) NSURL *URL;
@property (nonatomic,readonly) BOOL hasImage;
@property (nonatomic,getter = isFiltered) BOOL filtered;
@property (nonatomic,getter = isFailed) BOOL failed;

@end
